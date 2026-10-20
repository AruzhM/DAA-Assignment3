import csv
import math
from pathlib import Path

import matplotlib.pyplot as plt


# Paths
ROOT = Path(__file__).resolve().parent.parent
CSV_PATH = ROOT / "results" / "results.csv"
PLOTS_DIR = ROOT / "results" / "plots"

PLOTS_DIR.mkdir(parents=True, exist_ok=True)

ALGORITHMS = [
    "Kruskal",
    "Prim",
    "Kosaraju",
    "Kahn",
    "DagShortestPath"
]

SIZES = [1000, 3162, 10000, 31623, 100000]


# Read and validate CSV
with CSV_PATH.open(
    "r", newline="", encoding="utf-8-sig"
) as file:
    rows = list(csv.DictReader(file))

assert len(rows) == 50, (
    f"Expected 50 rows, found {len(rows)}"
)

for row in rows:
    for column in [
        "n", "m", "dsu_ops", "edge_scans",
        "comparisons", "max_depth", "result"
    ]:
        row[column] = int(row[column])

    row["time_ms"] = float(row["time_ms"])

    row["density"] = (
        "Sparse" if row["m"] == 4 * row["n"]
        else "Dense"
    )

    if row["n"] not in SIZES:
        raise ValueError("Unexpected graph size")

    if row["m"] not in (
        4 * row["n"], 20 * row["n"]
    ):
        raise ValueError("Unexpected density")

    # Sum of recorded operations.
    row["operations"] = (
        row["dsu_ops"]
        + row["edge_scans"]
        + row["comparisons"]
    )

keys = [
    (row["algorithm"], row["n"], row["density"])
    for row in rows
]

assert len(set(keys)) == 50, (
    "Missing or duplicate benchmark combinations"
)

for row in rows:
    n = row["n"]
    m = row["m"]
    algorithm = row["algorithm"]

    if algorithm == "Kosaraju":
        assert row["edge_scans"] == 2 * m

    if algorithm in ("Kahn", "DagShortestPath"):
        assert row["edge_scans"] == m


def get_data(algorithm, density):
    return sorted(
        [
            r for r in rows
            if r["algorithm"] == algorithm
            and r["density"] == density
        ],
        key=lambda r: r["n"]
    )


# GRAPH 1: Operations vs n
# Log-log axes, one line per algorithm and density.
plt.figure(figsize=(12, 7))

for algorithm in ALGORITHMS:
    for density in ("Sparse", "Dense"):
        data = get_data(algorithm, density)

        x = [r["n"] for r in data]
        y = [r["operations"] for r in data]

        plt.plot(
            x, y,
            marker="o",
            linestyle="-" if density == "Sparse" else "--",
            label=f"{algorithm} - {density}"
        )

plt.xscale("log")
plt.yscale("log")

plt.xlabel("Number of vertices (n)")
plt.ylabel("Counted operations")
plt.title("Operations vs Graph Size (Log-Log)")
plt.grid(True, which="both", alpha=0.3)
plt.legend(fontsize=8, ncol=2)
plt.tight_layout()

plt.savefig(
    PLOTS_DIR / "operations_vs_n.png",
    dpi=200
)
plt.close()


# GRAPH 2: Edge scans per edge
plt.figure(figsize=(11, 6))

for algorithm in ALGORITHMS:
    for density in ("Sparse", "Dense"):
        data = get_data(algorithm, density)

        x = [r["n"] for r in data]
        y = [
            r["edge_scans"] / r["m"]
            for r in data
        ]

        plt.plot(
            x, y,
            marker="o",
            linestyle="-" if density == "Sparse" else "--",
            label=f"{algorithm} - {density}"
        )

plt.xscale("log")

plt.xlabel("Number of vertices (n)")
plt.ylabel("edgeScans / m")
plt.title("Edge Scans per Edge")
plt.grid(True, alpha=0.3)
plt.legend(fontsize=8, ncol=2)
plt.tight_layout()

plt.savefig(
    PLOTS_DIR / "scans_per_edge.png",
    dpi=200
)
plt.close()


# GRAPH 3: Kruskal edge examination share
plt.figure(figsize=(10, 6))

for density in ("Sparse", "Dense"):
    data = get_data("Kruskal", density)

    x = [r["n"] for r in data]
    y = [
        r["edge_scans"] / r["m"]
        for r in data
    ]

    plt.plot(
        x, y,
        marker="o",
        linestyle="-" if density == "Sparse" else "--",
        label=density
    )

plt.xscale("log")
plt.ylim(0, 1.15)

plt.xlabel("Number of vertices (n)")
plt.ylabel("edgeScans / m")
plt.title("Kruskal: Share of Edges Examined")
plt.grid(True, alpha=0.3)
plt.legend()
plt.tight_layout()

plt.savefig(
    PLOTS_DIR / "kruskal_edge_share.png",
    dpi=200
)
plt.close()


# Log-log slope calculation
def log_log_slope(data, field):
    x = [math.log(r["n"]) for r in data]
    y = [math.log(r[field]) for r in data]

    avg_x = sum(x) / len(x)
    avg_y = sum(y) / len(y)

    numerator = sum(
        (a - avg_x) * (b - avg_y)
        for a, b in zip(x, y)
    )

    denominator = sum(
        (a - avg_x) ** 2 for a in x
    )

    return numerator / denominator


print("\nLOG-LOG SLOPE ANALYSIS")
print("-" * 55)

for algorithm in ALGORITHMS:
    for density in ("Sparse", "Dense"):
        data = get_data(algorithm, density)

        scans_slope = log_log_slope(
            data, "edge_scans"
        )

        operations_slope = log_log_slope(
            data, "operations"
        )

        print(
            f"{algorithm:16} {density:6} | "
            f"scans={scans_slope:.3f} | "
            f"operations={operations_slope:.3f}"
        )


# Counter vs complexity formula
print("\nCOUNTER / FORMULA RATIOS")
print("-" * 55)

for algorithm in ALGORITHMS:
    data = get_data(algorithm, "Sparse")

    print(f"\n{algorithm}:")

    for r in data:
        n = r["n"]
        m = r["m"]

        if algorithm == "Kruskal":
            ratio = r["dsu_ops"] / m
            formula = "dsuOps/E"

        elif algorithm == "Prim":
            ratio = r["edge_scans"] / (n + m)
            formula = "edgeScans/(V+E)"

        else:
            ratio = r["edge_scans"] / (n + m)
            formula = "edgeScans/(V+E)"

        print(
            f"n={n:6} | {formula}={ratio:.4f}"
        )


print("\nSUCCESS! Created 3 PNG plots:")

for name in [
    "operations_vs_n.png",
    "scans_per_edge.png",
    "kruskal_edge_share.png"
]:
    path = PLOTS_DIR / name
    print(f"  {path.name} ({path.stat().st_size} bytes)")
