"""
SortBenchmark.java 가 만든 benchmark_results.csv 를 읽어서 그래프를 그림

사용법 (4week 폴더에서):
    python plot_benchmark.py            # 이미 있는 csv 로 그래프만 그림
    python plot_benchmark.py --run      # 자바 벤치마크를 먼저 실행하고 그 결과로 그림
    python plot_benchmark.py 다른.csv    # 다른 csv 파일로 그림

그래프는 graphs/ 폴더에 png 로 저장됨
"""

import argparse
import csv
import math
import subprocess
from pathlib import Path

import matplotlib.pyplot as plt
import numpy as np
from matplotlib import font_manager
from matplotlib.lines import Line2D
from matplotlib.ticker import FuncFormatter, LogLocator

HERE = Path(__file__).resolve().parent
CSV_NAME = "benchmark_results.csv"

# 정렬 6개에 순서대로 붙일 색과 마커 (색만으로 구분하지 않도록 마커 모양도 다르게)
SERIES_COLORS = ["#2a78d6", "#eb6834", "#1baf7a", "#eda100", "#e87ba4", "#008300"]
MARKERS = ["o", "s", "^", "D", "v", "P"]

# 배경 / 글자 / 격자 색
SURFACE = "#fcfcfb"
INK = "#0b0b0b"
INK2 = "#52514e"
MUTED = "#898781"
GRID = "#e1e0d9"
AXIS = "#c3c2b7"

# 이 값(ms)보다 작은 측정값은 오차가 커서 지수 계산에서 뺌
MIN_MS_FOR_FIT = 0.1

# 지수를 계산할 때 쓸 점 개수 (가장 큰 n 부터)
FIT_POINTS = 3


def korean_font():
    """설치된 폰트 중 한글이 되는 걸 하나 고름 (윈도우: 맑은 고딕, 맥: AppleGothic)"""
    installed = {f.name for f in font_manager.fontManager.ttflist}
    for name in ["Malgun Gothic", "AppleGothic", "NanumGothic"]:
        if name in installed:
            return name
    return "sans-serif"


def setup_style():
    plt.rcParams.update({
        "font.family": korean_font(),
        "axes.unicode_minus": False,
        "figure.facecolor": SURFACE,
        "axes.facecolor": SURFACE,
        "savefig.facecolor": SURFACE,
        "axes.edgecolor": AXIS,
        "axes.labelcolor": INK2,
        "axes.titlecolor": INK,
        "axes.titlesize": 12,
        "xtick.color": MUTED,
        "ytick.color": MUTED,
        "text.color": INK,
        "axes.grid": True,
        "grid.color": GRID,
        "grid.linewidth": 0.6,
        "axes.spines.top": False,
        "axes.spines.right": False,
        "lines.linewidth": 2,
        "lines.markersize": 6,
    })


def run_java():
    """4week 폴더에서 자바를 컴파일하고 SortBenchmark 를 실행함 (csv 가 새로 만들어짐)"""
    java_files = [p.name for p in HERE.glob("*.java")]
    print("자바 컴파일 중...")
    subprocess.run(["javac", "-encoding", "UTF-8", *java_files], cwd=HERE, check=True)
    print("SortBenchmark 실행 중... (몇 분 걸릴 수 있음)")
    subprocess.run(["java", "-Dstdout.encoding=UTF-8", "SortBenchmark"], cwd=HERE, check=True)


def load(path):
    """csv 를 읽어서 data[(리스트 종류, 정렬)] = [(n, ms, 상태), ...] 로 돌려줌"""
    data = {}
    sorts, lists, sizes = [], [], []
    with open(path, encoding="utf-8-sig", newline="") as f:
        for row in csv.DictReader(f):
            n, lst, srt = int(row["n"]), row["list"], row["sort"]
            if srt not in sorts:
                sorts.append(srt)
            if lst not in lists:
                lists.append(lst)
            if n not in sizes:
                sizes.append(n)
            if row["ms"]:  # skip / SO / WRONG 은 시간이 없으니 뺌
                data.setdefault((lst, srt), []).append((n, float(row["ms"]), row["status"]))
    return data, sorts, lists, sizes


def exponent(points):
    """log(시간) = k * log(n) + c 로 직선을 맞춰서 기울기 k 를 구함
    k 가 2 근처면 O(n²), 1.0 ~ 1.2 근처면 O(n log n), 1 근처면 O(n)
    작은 n 은 측정 오차가 커서, 큰 n 쪽 FIT_POINTS 개만 씀"""
    pts = sorted((n, ms) for n, ms, _ in points if ms >= MIN_MS_FOR_FIT)[-FIT_POINTS:]
    if len(pts) < 2:
        return None
    x = np.log([n for n, _ in pts])
    y = np.log([ms for _, ms in pts])
    return np.polyfit(x, y, 1)[0]


def format_n(n):
    return f"{n // 1000}k" if n % 1000 == 0 else str(n)


def plot_grid(data, panels, lines, key, sizes, title, out_path):
    """작은 그래프 여러 개를 격자로 그림
    panels: 그래프 하나당 하나씩 (예: 리스트 종류)
    lines : 그래프 안의 선들 (예: 정렬)
    key(panel, line) -> data 의 키"""
    cols = 3
    rows = math.ceil(len(panels) / cols)
    fig, axes = plt.subplots(rows, cols, figsize=(5 * cols, 4 * rows),
                             sharex=True, sharey=True, squeeze=False)

    for ax, panel in zip(axes.flat, panels):
        for i, line in enumerate(lines):
            # 0ms 는 로그 축에 못 그리니까 뺌
            pts = [(n, ms, st) for n, ms, st in data.get(key(panel, line), []) if ms > 0]
            if not pts:
                continue
            color, marker = SERIES_COLORS[i % len(SERIES_COLORS)], MARKERS[i % len(MARKERS)]
            ax.plot([p[0] for p in pts], [p[1] for p in pts],
                    color=color, marker=marker, markeredgecolor=SURFACE, markeredgewidth=1)

            # 시간 초과로 1회만 잰 점은 속이 빈 마커로 표시
            limit = [p for p in pts if p[2] == "limit"]
            if limit:
                ax.plot([p[0] for p in limit], [p[1] for p in limit], linestyle="none",
                        marker=marker, markerfacecolor=SURFACE, markeredgecolor=color,
                        markeredgewidth=1.5, markersize=7)

        ax.set_title(panel, loc="left")
        ax.set_xscale("log")
        ax.set_yscale("log")
        ax.set_xticks(sizes)
        ax.set_xticklabels([format_n(n) for n in sizes])
        ax.minorticks_off()
        # 10^-1 같은 지수 표기 대신 0.1, 1, 10 처럼 그냥 숫자로 표시
        ax.yaxis.set_major_locator(LogLocator(base=10))
        ax.yaxis.set_major_formatter(FuncFormatter(lambda v, _: f"{v:g}"))

    # 남는 칸은 숨김
    for ax in axes.flat[len(panels):]:
        ax.set_visible(False)

    for ax in axes[-1]:
        ax.set_xlabel("n (리스트 길이)")
    for ax in axes[:, 0]:
        ax.set_ylabel("시간 (ms, 로그 스케일)")

    # 범례는 맨 위에 한 번만
    handles = [Line2D([], [], color=SERIES_COLORS[i % len(SERIES_COLORS)],
                      marker=MARKERS[i % len(MARKERS)], markeredgecolor=SURFACE, label=line)
               for i, line in enumerate(lines)]
    handles.append(Line2D([], [], color=MUTED, marker="o", linestyle="none",
                          markerfacecolor=SURFACE, label="빈 마커: 1회만 측정"))
    fig.legend(handles=handles, loc="upper center", ncol=len(handles), frameon=False,
               bbox_to_anchor=(0.5, 0.965), labelcolor=INK2)

    fig.suptitle(title, x=0.01, y=0.995, ha="left", fontsize=15, color=INK)
    fig.text(0.01, 0.955, "두 축 모두 로그 스케일 · 기울기가 2면 O(n²), 1 근처면 O(n log n)",
             ha="left", fontsize=10, color=INK2)
    fig.tight_layout(rect=(0, 0, 1, 0.92))
    fig.savefig(out_path, dpi=150)
    print("저장:", out_path)
    return fig


def plot_exponents(data, sorts, lists, out_path):
    """정렬 x 리스트 종류별 기울기(지수)를 표 모양 히트맵으로 그림"""
    grid = np.full((len(sorts), len(lists)), np.nan)
    for r, srt in enumerate(sorts):
        for c, lst in enumerate(lists):
            k = exponent(data.get((lst, srt), []))
            if k is not None:
                grid[r, c] = k

    fig, ax = plt.subplots(figsize=(1.5 * len(lists) + 2, 0.7 * len(sorts) + 1.8))
    cmap = plt.get_cmap("Blues").copy()
    cmap.set_bad(GRID)
    im = ax.imshow(grid, cmap=cmap, vmin=0.8, vmax=2.2, aspect="auto")

    for r in range(len(sorts)):
        for c in range(len(lists)):
            k = grid[r, c]
            if np.isnan(k):
                ax.text(c, r, "너무 빠름", ha="center", va="center", fontsize=9, color=INK2)
            else:
                ax.text(c, r, f"{k:.2f}", ha="center", va="center", fontsize=11,
                        color="white" if k > 1.6 else INK)

    ax.set_xticks(range(len(lists)))
    ax.set_xticklabels(lists)
    ax.set_yticks(range(len(sorts)))
    ax.set_yticklabels(sorts)
    ax.tick_params(colors=INK2, length=0)
    ax.grid(False)
    for spine in ax.spines.values():
        spine.set_visible(False)
    # 칸 사이에 배경색 줄을 넣어서 구분
    ax.set_xticks(np.arange(-0.5, len(lists)), minor=True)
    ax.set_yticks(np.arange(-0.5, len(sorts)), minor=True)
    ax.grid(which="minor", color=SURFACE, linewidth=2)
    ax.tick_params(which="minor", length=0)

    cbar = fig.colorbar(im, ax=ax, fraction=0.04, pad=0.02)
    cbar.set_label("기울기 (지수)", color=INK2)
    cbar.outline.set_visible(False)

    ax.set_title("n 에 대한 시간의 지수 (log-log 기울기)\n"
                 "약 2 → O(n²),  약 1.1 → O(n log n),  '너무 빠름' → 측정값이 "
                 f"{MIN_MS_FOR_FIT}ms 미만 (거의 O(n))",
                 loc="left", fontsize=11, color=INK)
    fig.tight_layout()
    fig.savefig(out_path, dpi=150)
    print("저장:", out_path)
    return fig


def print_exponents(data, sorts, lists):
    print()
    # 윈도우 콘솔(cp949)에서 깨지지 않도록 특수문자 없이 출력
    print("기울기(지수) 표 : 2 근처 = O(n^2),  1.0~1.2 = O(n log n),  '-' = 너무 빨라서 계산 불가")
    print(f"{'':10}" + "".join(f"{l:>10}" for l in lists))
    for srt in sorts:
        cells = []
        for lst in lists:
            k = exponent(data.get((lst, srt), []))
            cells.append(f"{k:10.2f}" if k is not None else f"{'-':>10}")
        print(f"{srt:10}" + "".join(cells))
    print()


def main():
    parser = argparse.ArgumentParser(description="정렬 벤치마크 결과 그래프")
    parser.add_argument("csv", nargs="?", default=str(HERE / CSV_NAME), help="읽을 csv 파일")
    parser.add_argument("--run", action="store_true", help="자바 벤치마크를 먼저 실행")
    parser.add_argument("--no-show", action="store_true", help="창은 띄우지 않고 png 만 저장")
    args = parser.parse_args()

    if args.run:
        run_java()

    csv_path = Path(args.csv)
    if not csv_path.exists():
        print(f"{csv_path} 가 없어요. 먼저 'java SortBenchmark' 를 실행하거나 --run 을 붙여 주세요.")
        return

    setup_style()
    data, sorts, lists, sizes = load(csv_path)
    out_dir = HERE / "graphs"
    out_dir.mkdir(exist_ok=True)

    # 1) 입력 종류별로 정렬들을 비교 (같은 입력에서 어떤 정렬이 빠른가)
    plot_grid(data, lists, sorts, lambda panel, line: (panel, line), sizes,
              "입력 종류별 정렬 시간 비교", out_dir / "by_input.png")

    # 2) 정렬별로 입력 종류를 비교 (최선 / 평균 / 최악의 경우)
    plot_grid(data, sorts, lists, lambda panel, line: (line, panel), sizes,
              "정렬별 입력 종류에 따른 시간 변화", out_dir / "by_sort.png")

    # 3) 기울기(지수) 표
    plot_exponents(data, sorts, lists, out_dir / "exponents.png")
    print_exponents(data, sorts, lists)

    if not args.no_show:
        plt.show()


if __name__ == "__main__":
    main()
