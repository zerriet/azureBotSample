import os
import sys
from setfit import SetFitModel

os.environ["TOKENIZERS_PARALLELISM"] = "false"

# Define all trained labels
labels = ["check_balance", "check_account_details", "unknown"]

# Construct model path
project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
model_path = os.path.join(project_root, "setfit_model")

print(f"Model path: {model_path}")
print("Exists?", os.path.isdir(model_path))

try:
    model = SetFitModel.from_pretrained(model_path, labels=labels, local_files_only=True)
    user_input = sys.argv[1]
    pred = model.predict([user_input])[0]
    print(pred)
    
except Exception as e:
    print("ERROR:", e)
    sys.exit(1)
