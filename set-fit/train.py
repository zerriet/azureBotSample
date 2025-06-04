
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

train_data = Dataset.from_dict({
"text": [

    # --- check_balance ---
    "What's my balance?",
    "How much money do I have?",
    "Show my account balance",
    "Balance check",
    "Can I see how much is in my account?",
    "I'm broke af how much left",
    "yo what's my bank sayin",
    "Any cash left?",
    "Account balance pls",
    "Check wallet",
    "How much dough I got?",
    "My funds?",
    "I wanna know my money status",
    "Cash check pls",
    "Lowkey how much do I have?",
    "Can you show me my funds?",
    "What's left in the bank?",
    "How stacked am I?",
    "Money status update?",
    "Update me on my cash",

    # --- check_account_details ---
    "What's my account number?",
    "Show me my account info",
    "Give me account details",
    "Where can I see my account number?",
    "Display account information",
    "Acc no pls",
    "What's my digits?",
    "Drop my account info",
    "Tell me my full bank details",
    "My banking credentials?",
    "Account deets pls",
    "Yo what's my acc id?",
    "Wanna see my bank info",
    "Hit me with my acc no",
    "What acc do I have?",
    "Where's my bank ID?",
    "Reveal account credentials",
    "Pull up account details",
    "Need my banking profile",
    "Spill my account info",

    # --- unknown ---
    "Tell me a joke",
    "I want pizza",
    "What's the weather today?",
    "Book me a cab",
    "Turn on the lights",
    "Let's vibe",
    "Send a meme",
    "How tall is the Eiffel Tower?",
    "Who's Taylor Swift?",
    "Make me laugh",
    "Tell me sum funny",
    "Wanna hear a story",
    "Spotify play",
    "Flip a coin",
    "U up?",
    "Open Instagram",
    "What's trending now?",
    "Rate my drip",
    "Where's the party?",
    "Run a beat",

    # --- quiz_me ---
    "Quiz me on something",
    "Hit me with a quiz",
    "Test my knowledge",
    "Ask me questions",
    "Gimme a quiz",
    "Can you quiz me?",
    "Challenge me with questions",
    "Pop a question",
    "I'm ready for a test",
    "Ask me trivia",
    "Shoot me a random question",
    "Hit me with some brain fuel",
    "Yo throw me a quiz",
    "I wanna flex my brain",
    "Give me a riddle"
]
,
    "label": [
        *([0] * 20),  # check_balance
        *([1] * 20),  # check_account_details
        *([2] * 20),   # unknown
        *([3] * 20)   # quiz_me
    ]
})


# Define labels
labels = ["check_balance", "check_account_details", "unknown",  "quiz_me"]


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
    "Set a timer for 10 minutes",

    "quiz me bro",
    "i wanna be quizzed"
]

print("\n Generalization Test Results:")
preds = model.predict(test_texts)

for text, pred in zip(test_texts, preds):
    print(f'> "{text}" → {pred}')
