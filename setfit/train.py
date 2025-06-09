# Compatibility + Warning Fixes

import multiprocessing
multiprocessing.set_start_method("fork", force=True)

import os

os.environ["KMP_DUPLICATE_LIB_OK"] = "TRUE"
os.environ["TOKENIZERS_PARALLELISM"] = "false"
os.environ["LC_ALL"] = "en_US.UTF-8"
os.environ["LANG"] = "en_US.UTF-8"
os.environ["OMP_NUM_THREADS"] = "1"

# Core Imports

from setfit import SetFitModel, Trainer, TrainingArguments
from datasets import Dataset

# Step 1: Training data

texts = [
    # Paynow
    "Transfer $10 to Sarah using PayNow",
    "I wanna PayNow my buddy real quick",
    "Can you zap $20 to Ben?",
    "Send some cash to John via PayNow",
    "Let's PayNow that money now",

    # Scan and Pay
    "I want to scan and pay for this",
    "Gimme the QR, I'll pay now",
    "Let me scan to pay the bill",
    "Can I just scan this and pay?",
    "I'll use Scan & Pay, faster",

    # Transaction History
    "Show me what I spent recently",
    "Need to check my money trail",
    "Pull up my past transactions",
    "Can I see where my cash went?",
    "What's my recent spending like?",

    # Check Balance
    "How much do I have left?",
    "Lemme see my balance real quick",
    "Check account balance please",
    "Am I broke or nah",
    "Show me my funds",

    # Logout
    "I'm done, log me out",
    "Time to bounce, sign me out",
    "Can you log me off?",
    "Exit the app please",
    "I wanna logout now",

    # Money Lock
    "Lock $100 into savings mode",
    "Put my money somewhere safe",
    "Can you stash $50 so I don't spend it?",
    "Time to lock some funds",
    "Save and lock this amount before I blow it",

    # Generic
    "Order me a pizza please",
    "Tell me a joke",
    "What's the weather like in Tokyo?",
    "Play some music",
    "Can you set a timer?",

    # Generic Bank
    "Quiz me on financial literacy",
    "What's budgeting bro?",
    "Teach me how to budget",
    "Tell me more about savings accounts",
    "What's a fixed deposit?",

    # Deals
    "Got any promos running now?",
    "Hook me up with some deals",
    "I want rewards or cashback",
    "Any offers for me today?",
    "What perks do I get?",

    # Refer a Friend
    "How do I invite someone?",
    "Wanna invite a friend, where's the link?",
    "Referral rewards — how does it work?",
    "Can I share this app to earn something?",
    "I wanna refer my buddy, help me out",
]

labels = [
    "paynow",
    "paynow",
    "paynow",
    "paynow",
    "paynow",

    "scan_and_pay",
    "scan_and_pay",
    "scan_and_pay",
    "scan_and_pay",
    "scan_and_pay",

    "transaction_history",
    "transaction_history",
    "transaction_history",
    "transaction_history",
    "transaction_history",

    "check_balance",
    "check_balance",
    "check_balance",
    "check_balance",
    "check_balance",

    "logout",
    "logout",
    "logout",
    "logout",
    "logout",

    "money_lock",
    "money_lock",
    "money_lock",
    "money_lock",
    "money_lock",

    "generic",
    "generic",
    "generic",
    "generic",
    "generic",

    "generic_bank",
    "generic_bank",
    "generic_bank",
    "generic_bank",
    "generic_bank",

    "deals",
    "deals",
    "deals",
    "deals",
    "deals",

    "refer_a_friend",
    "refer_a_friend",
    "refer_a_friend",
    "refer_a_friend",
    "refer_a_friend",
]

train_data = Dataset.from_dict({
    "text": texts,
    "label": labels
})

# Step 2: Define labels
unique_labels = sorted(list(set(labels)))

# Step 3: Load Model

model = SetFitModel.from_pretrained(
    "BAAI/bge-small-en-v1.5",
    labels=unique_labels
)

# Step 4: Training Args

args = TrainingArguments(
    batch_size=8,
    num_epochs=10,
)

# Step 5: Train Model

trainer = Trainer(
    model=model,
    args=args,
    train_dataset=train_data,
)
trainer.train()

# Step 6: Save Model

model.save_pretrained("setfit_model")
print(" Model trained and saved at ./setfit_model")


# Optional test prediction code (assumes `test_texts` and `preds` exist)
# for text, pred in zip(test_texts, preds):
#     print(f'> "{text}" → {pred}')
