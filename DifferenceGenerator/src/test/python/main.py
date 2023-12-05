import os
import optuna
import subprocess

TRIALS = 100
# THREADS = 3 #: not supported from gradle, hence not used

class optimizer():
    def __init__(self) -> None:
        filePath = os.path.dirname(os.path.abspath(__file__))
        filePath = os.path.join(filePath, "db.sqlite3")
        self.study =  optuna.create_study(storage="sqlite:///" + filePath,
                                         study_name="Gotoh",
                                         load_if_exists=True,
                                         direction="minimize")
        self.run()

    def run(self) -> None:
        self.study.optimize(self.blackBox, n_trials=TRIALS)
        
    
    
    def blackBox(self, trial) -> float:
        script_directory = os.path.dirname(os.path.abspath(__file__)) # /python
        script_directory = os.path.dirname(script_directory)# /test
        script_directory = os.path.dirname(script_directory)# /src
        script_directory = os.path.dirname(script_directory)# /DifferenceGenerator

        gapOpen = trial.suggest_float("gapOpen", -1.0, 0.5)
        gapExtend = trial.suggest_float("gapExtend", -1.0, 0.5 )

        command = 'gradlew test --tests "DifferenceGeneratorTest.Test a generated case using TestCaseGenerator" --info '
        command = command + "-DgapOpenPenalty=" +'"' +str(gapOpen)+'"'
        command = command + " -DgapExtensionPenalty=" + '"' +str(gapExtend) + '"'

        result = subprocess.run(command, cwd=script_directory, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        
        out = result.stdout.decode("utf-8").split("\n")
        out = [i for i in out if "Levenshtein" in i]
        out = float(out[0].strip().split(" ")[-1])

        return out
        
        


if __name__ == "__main__":
    # run while true
    # stop only on keyboard interrupt otherwise run even on crash
    while True:
        try:
            optimizer()
        except Exception as e:
            continue
        except KeyboardInterrupt:
            break