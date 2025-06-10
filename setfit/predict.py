import os
from setfit import SetFitModel

#  Environment setup
os.environ["LC_ALL"] = "en_US.UTF-8"
os.environ["LANG"] = "en_US.UTF-8"
os.environ["OMP_NUM_THREADS"] = "1"
os.environ["KMP_DUPLICATE_LIB_OK"] = "TRUE"
os.environ["TOKENIZERS_PARALLELISM"] = "false"

#  Load model
model_path = os.path.join(os.path.dirname(__file__), "setfit_model")
model = SetFitModel._from_pretrained(model_id=model_path)

#  Full test data
test_data = [

    # ---- Transaction History ----
    ("How much I spent last week?", "transaction history"),
    ("My activity last week", "transaction history"),
    ("How much did I shell out last week?", "transaction history"),
    ("My total expenditure last week?", "transaction history"),
    ("What's the sum of my expenses?", "transaction history"),
    ("How much did I pay out last week?", "transaction history"),
    ("What was the damage to my wallet last week?", "transaction history"),
    ("What was my total for last week?", "transaction history"),
    ("Damage today", "transaction history"),
    ("How much did I blow last week?", "transaction history"),
    ("Cashflow today", "transaction history"),
    ("Quick rundown of last week's expenses", "transaction history"),
    ("Transaction History", "transaction history"),
    ("History", "transaction history"),
    ("Money score", "transaction history"),
    ("Financial footprint", "transaction history"),
    ("My spending spree", "transaction history"),
    ("How much did I swipe last week?", "transaction history"),
    ("Can you spill the tea on my spending last week?", "transaction history"),
    ("How much did I fork out last week?", "transaction history"),
    ("What was my weekly splurge total?", "transaction history"),
    ("What's the scoop on my last week's expenses?", "transaction history"),
    ("Last week how much Benjamins?", "transaction history"),

    # ---- Check Balance ----
    ("Peep my balance", "check balance"),
    ("What's my cash status", "check balance"),
    ("How much is in the bank", "check balance"),
    ("Check my dough", "check balance"),
    ("What's the balance vibe", "check balance"),
    ("How much I got chillin'", "check balance"),
    ("Let's see my funds", "check balance"),
    ("What's my money count", "check balance"),
    ("Check my wallet vibes", "check balance"),
    ("How's my cash flow looking", "check balance"),
    ("peep my cash vibes?", "check balance"),
    ("Check Balance", "check balance"),
    ("Dough status", "check balance"),
    ("Checking my stash", "check balance"),
    ("What's the tea, money fam", "check balance"),
    ("See my balance without the drama", "check balance"),
    ("See balance", "check balance"),
    ("Deets on my balance", "check balance"),

    # ---- Logout ----
    ("Time to bounce!", "logout"),
    ("Logging off, fam!", "logout"),
    ("Peace out, digital world!", "logout"),
    ("Catch ya later, tech squad!", "logout"),
    ("Signing off, my peeps!", "logout"),
    ("Exiting the chat, see ya!", "logout"),
    ("Time to ghost!", "logout"),
    ("Logging out like a boss!", "logout"),
    ("Switching off the vibes!", "logout"),
    ("Closing the laptop portal!", "logout"),
    ("Time to unplug, my dudes!", "logout"),
    ("Bye-bye, cyberspace!", "logout"),
    ("Logging off this adventure!", "logout"),
    ("Signing out, keep it real!", "logout"),
    ("Time to dip from the digital scene!", "logout"),
    ("Shutting down my online self!", "logout"),
    ("Logging off the grid!", "logout"),
    ("Time to hit the offline zone!", "logout"),
    ("Closing the app, see ya later!", "logout"),
    ("Exiting the matrix!", "logout"),
    ("Time to log off the hype train!", "logout"),
    ("Signing out of the digital jungle!", "logout"),
    ("Hitting the exit button on this session!", "logout"),
    ("Time to step away from the screen!", "logout"),
    ("Logging off the internet express!", "logout"),
    ("Time to close the digital door!", "logout"),
    ("Taking a break from the online hustle!", "logout"),
    ("Signing off this virtual ride!", "logout"),
    ("Time to fade into the offline world!", "logout"),
    ("Exiting the digital universe!", "logout"),
    ("Exit", "logout"),

    # ---- Money Lock ----
    ("Money Lock $10", "money lock"),
    ("Secure the cheddar!", "money lock"),
    ("Fortify my funds!", "money lock"),
    ("Cash in the vault, please!", "money lock"),
    ("Seal the green!", "money lock"),
    ("Money on lockdown!", "money lock"),
    ("Stash my stash!", "money lock"),
    ("Cage the coins!", "money lock"),
    ("Guard my bucks!", "money lock"),
    ("Put my dough on ice!", "money lock"),
    ("Bunker my bank!", "money lock"),
    ("Hide the hundo!", "money lock"),
    ("Bolt down the bills!", "money lock"),
    ("Wrap my wealth!", "money lock"),
    ("Keep my cash cozy!", "money lock"),
    ("Lock up the loot!", "money lock"),
    ("Secure my savings!", "money lock"),
    ("Nest my nest egg!", "money lock"),
    ("Put the paper on pause!", "money lock"),
    ("Crate my cash!", "money lock"),
    ("Shield my shekels!", "money lock"),
    ("Stow the stacks!", "money lock"),
    ("Cloak my coins!", "money lock"),
    ("Panic room for my pennies!", "money lock"),
    ("Tuck away the treasure!", "money lock"),
    ("Jam the jackpot!", "money lock"),
    ("Block my bucks!", "money lock"),
    ("Wrap the wallet!", "money lock"),
    ("Cocoon my cash!", "money lock"),
    ("Bunker down the bills!", "money lock"),
    ("Seal the stash!", "money lock"),

    # ---- Generic ----
    ("How are you?", "generic"),
    ("What's the weather?", "generic"),
    ("How are you vibing today?", "generic"),
    ("How are you feeling in the digital realm?", "generic"),
    ("How are you holding up in the cloud?", "generic"),
    ("How are you processing the good vibes?", "generic"),
    ("How are you cruising through cyberspace?", "generic"),
    ("How are you surfing the info waves?", "generic"),
    ("How are you rocking the ones and zeros?", "generic"),
    ("How are you handling the data dance?", "generic"),
    ("How are you zipping through the chats?", "generic"),
    ("How are you jamming with the algorithms?", "generic"),
    ("How are you keeping it real in the matrix?", "generic"),
    ("How are you decoding the day?", "generic"),
    ("How are you navigating the net?", "generic"),
    ("How are you managing the meme machine?", "generic"),
    ("How are you tuning into the trends?", "generic"),
    ("How are you blending the bytes?", "generic"),
    ("How are you thriving in the tech jungle?", "generic"),
    ("How are you connecting with the cool kids?", "generic"),
    ("How are you scrolling through the info feed?", "generic"),
    ("How are you keeping the convo lit?", "generic"),
    ("What's bussin?", "generic"),
    ("I am broke", "generic"),
    ("Do you guys have a dope banking app? I need something that slaps!", "generic"),
    ("Where do I start?", "generic"),
    ("How ya feel?", "generic"),
    ("What's good?", "generic"),


    # ---- Deals ----
    ("Epic deals", "deals"),
    ("Hot tips on where to snag some epic deals", "deals"),
    ("Where can I score the coolest discounts", "deals"),
    ("Latest scoops", "deals"),
    ("Scoop on the freshest deals", "deals"),
    ("What;s poppin'", "deals"),
    ("Any secret spots for snagging sweet discounts", "deals"),
    ("Latest buzz on cashback offers?", "deals"),
    ("Latest buzz", "deals"),
    ("Ultimate savings deals", "deals"),
    ("Promo land", "deals"),
    ("Promotions", "deals"),

    # ---- Refer a Friend ----
    ("Refer a friend", "refer a friend"),
    ("Refer my friends", "refer a friend"),
    ("Make a chum connection and enjoy", "refer a friend"),
    ("Hook up my pal", "refer a friend"),
    ("Invite my crew", "refer a friend"),
    ("Grab my pals to join me", "refer a friend"),
    ("Spread the friendship vibes", "refer a friend"),
    ("My homie to join me", "refer a friend"),
    ("Share the love with my BFF benefits!", "refer a friend"),
    ("Share awesome perks", "refer a friend"),
    ("Compadres for cool rewards!", "refer a friend"),
    ("Bring my buddies", "refer a friend"),
    ("unlock exclusive deals with my homies", "refer a friend"),
    ("Referral", "refer a friend"),
]

#  Extract inputs and labels
sample_inputs = [x[0] for x in test_data]
true_labels = [x[1].replace(" ", "_") for x in test_data]
label_list = sorted(set(true_labels))

#  Predictions
preds = model.predict(sample_inputs)
pred_indices = [label_list.index(p) for p in preds]
true_indices = [label_list.index(l) for l in true_labels]

# Section Accuracy
print("\n Section Accuracy:")
for label in label_list:
    indices = [i for i, true in enumerate(true_labels) if true == label]
    correct = sum(1 for i in indices if preds[i] == true_labels[i])
    print(f"{label}: {correct}/{len(indices)} correct")

#  Prediction Breakdown
print("\n Prediction Breakdown:")
for i, (text, pred_str, true_str) in enumerate(zip(sample_inputs, preds, true_labels)):
    correct = "✅" if pred_str == true_str else "❌"
    print(f'{i+1:02d}. "{text}" → Predicted: {pred_str} | Expected: {true_str} {correct}')

# Overall Accuracy
total_correct = sum(1 for p, t in zip(preds, true_labels) if p == t)
print(f"\n🎯 Overall Accuracy: {total_correct}/{len(test_data)} correct")
