import os
import sys
from setfit import SetFitModel

# Disable parallel tokenizers
os.environ["TOKENIZERS_PARALLELISM"] = "false"

# Define labels
labels = ["check_balance", "check_account_details"]

# Correct relative model path (one folder up)
project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
model_path = os.path.join(project_root, "setfit_model")

print(f"Model path: {model_path}")
print("Exists?", os.path.isdir(model_path))  

# Load model from local
model = SetFitModel.from_pretrained(model_path, labels=labels, local_files_only=True)

# Predict
user_input = sys.argv[1]
pred = model.predict([user_input])[0]
print(pred)
