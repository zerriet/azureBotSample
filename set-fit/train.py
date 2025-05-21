
# Compatibility + Warning Fixes

import multiprocessing
multiprocessing.set_start_method("fork", force=True)

import os
os.environ["TOKENIZERS_PARALLELISM"] = "false"

# Core Imports

from setfit import SetFitModel, Trainer, TrainingArguments
from datasets import Dataset

# Step 1: Training data

train_data = Dataset.from_dict({
    "text": [
        # check_balance
        "What's my balance?",
        "How much money do I have?",
        "Show my account balance",
        "Balance check",
        "Can I see how much is in my account?",

        # check_account_details
        "What's my account number?",
        "Show me my account info",
        "Give me account details",
        "Where can I see my account number?",
        "Display account information",

        # unknown
        "Tell me a joke",
        "I want pizza",
        "What's the weather today?",
        "Book me a cab",
        "Turn on the lights"
    ],
    "label": [
        0, 0, 0, 0, 0,  # check_balance
        1, 1, 1, 1, 1,  # check_account_details
        2, 2, 2, 2, 2   # unknown
    ]
})

# Define labels
labels = ["check_balance", "check_account_details", "unknown"]


# Step 2: Load Model

model = SetFitModel.from_pretrained(
    "BAAI/bge-small-en-v1.5",
    labels=labels
)


# Step 3: Training Args

args = TrainingArguments(
    batch_size=16,
    num_epochs=10,
)


# Step 4: Train Model

trainer = Trainer(
    model=model,
    args=args,
    train_dataset=train_data,
)
trainer.train()


# Step 5: Save Model

model.save_pretrained("setfit_model")
print(" Model trained and saved at ./setfit_model")


# Step 6: Test Generalization

test_texts = [
    "Could you show how much cash I have?",
    "What's left in my bank account?",
    "Do I have any money?",
    "Tell me my available balance",
    "How much funds do I currently have?",

    "Can you fetch my account number?",
    "Give me my bank account information",
    "I want to see my account ID",
    "What are the details of my account?",
    "Tell me about my account credentials",


    "Order sushi",
    "Play music please",
    "Turn off the fan",
    "Set a timer for 10 minutes"
]

print("\n Generalization Test Results:")
preds = model.predict(test_texts)

for text, pred in zip(test_texts, preds):
    print(f'> "{text}" → {pred}')
