import os

# 🔧 Environment + OpenMP fix
os.environ["LC_ALL"] = "en_US.UTF-8"
os.environ["LANG"] = "en_US.UTF-8"
os.environ["OMP_NUM_THREADS"] = "1"
os.environ["KMP_DUPLICATE_LIB_OK"] = "TRUE"
os.environ["TOKENIZERS_PARALLELISM"] = "false"

from flask import Flask, request, jsonify
from setfit import SetFitModel

app = Flask(__name__)


model_path = os.path.join(os.path.dirname(__file__), "setfit_model")
model = SetFitModel._from_pretrained(model_id=model_path)

@app.route("/predict", methods=["POST"])
def predict():
    data = request.get_json()
    user_input = data.get("userInput", "")
    if not user_input:
        return jsonify({"error": "Missing input"}), 400
    try:
        prediction = model.predict([user_input])[0]
        return jsonify({"prediction": prediction})
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5001)
