const chat = document.querySelector("#chat");
const input = document.querySelector("#input");
const sendButton = document.querySelector("#send-button");

function parseHistory() {
    const history = Array.from(document.querySelectorAll(".bubble__chat--bot:not(:first-child)"));
    history.forEach((message) => {
        const content = message.textContent;
        message.innerHTML = marked.parse(content);
    });
}

document.addEventListener("DOMContentLoaded", parseHistory);

sendButton.addEventListener("click", sendMessage);

input.addEventListener("keyup", function (event) {
    event.preventDefault();
    if (event.keyCode === 13) {
        sendButton.click();
    }
});

document.addEventListener("DOMContentLoaded", goToBottom);

const loadingAnimation = '<div class="typing-animation"><span>&nbsp;</span><span>&nbsp;</span><span>&nbsp;</span></div>';

function toggleInputState(disabled) {
    input.disabled = disabled;
    sendButton.disabled = disabled;
    input.placeholder = disabled ? "Wait for the answer" : "Send a message";
}

async function sendMessage() {
    if (input.value === "" || input.value == null) return;

    const message = input.value;
    input.value = "";

    toggleInputState(true);

    const newBubble = createUserBubble();
    newBubble.innerHTML = message;
    chat.appendChild(newBubble);

    let newBubbleBot = createBubbleBot();
    newBubbleBot.innerHTML = loadingAnimation;
    chat.appendChild(newBubbleBot);
    goToBottom();

    fetch(`${window.location.origin}/chat`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({question: message}),
    })
        .then(async (response) => {
            if (!response.ok) {
                throw new Error("An error occurred while trying to send the message.");
            }

            const answerReader = response.body.getReader();
            let partialAnswer = "";

            while (true) {
                const {done, value: chunk} = await answerReader.read();

                if (done) break;

                partialAnswer += new TextDecoder().decode(chunk);
                newBubbleBot.innerHTML = marked.parse(partialAnswer);
                goToBottom();
            }
        })
        .catch((error) => {
            console.error("Error:", error);
            newBubbleBot.innerHTML =
                "Sorry, an error occurred while processing your message.";
        }).finally(() => {
            toggleInputState(false);
        });
}

function createUserBubble() {
    const bubble = document.createElement("p");
    bubble.className = "bubble__chat bubble__chat--user";
    return bubble;
}

function createBubbleBot() {
    const bubble = document.createElement("p");
    bubble.className = "bubble__chat bubble__chat--bot";
    return bubble;
}

function goToBottom() {
    chat.scrollTop = chat.scrollHeight;
}
