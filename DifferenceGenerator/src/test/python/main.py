import sys
import optuna
import subprocess

TRIALS = 100
THREADS = 4

class optimizer():
    def __init__(self, jarPath) -> None:
        self.study =  optuna.create_study(storage="sqlite:///db.sqlite3",
                                         study_name="Gotoh",
                                         load_if_exists=True,
                                         direction="maximize")
        self.jarPath = jarPath
        self.run()

    def run(self) -> None:
        self.study.optimize(self.blackBox, n_trials=TRIALS, n_jobs=THREADS)
        
        
    def prepareCommand(self, trial) -> str:
        gapOpen = trial.suggest_float("gapOpen", -10.0, 0.0)
        gapExtend = trial.suggest_float("gapExtend", -10.0, 0.0)
        command = "java -jar " + self.jarPath + " " + str(gapOpen) + " " + str(gapExtend)  
        return command
    
    def blackBox(self, trial) -> float:
        command = self.prepareCommand(trial, command)
        result = subprocess.run(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        return float(result.stdout.decode("utf-8").split("\n")[0])
        
        
        
        
        


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python main.py <jarPath>")
        sys.exit(1)
    optimizer(sys.argv[1])