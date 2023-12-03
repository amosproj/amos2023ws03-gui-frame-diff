# Optuna optimizer

An optimizer of Gotoh Parameters using Optuna.

## Requirements

- Python 3.6 or later
- [Optuna](https://optuna.org/)
- [Optuna Dashboard](https://github.com/optuna/optuna-dashboard) (optional for visualization)

## Usage

run `python3 main.py` to start optimization.
If not already created, creates a .db in the python directory.

## Visualization

run `optuna-dashboard sqlite:///db.sqlite3` to start visualization. <br>
alternatively use this web app: [https://optuna.github.io/optuna-dashboard/](https://optuna.github.io/optuna-dashboard/)
