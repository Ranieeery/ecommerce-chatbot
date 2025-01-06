const chat = document.querySelector('#chat');
const input = document.querySelector('#input');
const sendButton = document.querySelector('#button-send');

sendButton.addEventListener('click', sendMessage);

input.addEventListener('keyup', function (event) {
    event.preventDefault();
    if (event.keyCode === 13) {
        sendButton.click();
    }
});

document.addEventListener('DOMContentLoaded', goToBottom);

async function sendMessage() {
    if (input.value === '' || input.value == null) return;

    const message = input.value;
    input.value = '';

    const newBubble = createUserBubble();
    newBubble.innerHTML = message;
    chat.appendChild(newBubble);

    let newBubbleBot = createBubbleBot();
    chat.appendChild(newBubbleBot);
    goToBottom();

    fetch('http://localhost:8080/chat', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({'Question': message}),
    }).then(async response => {
        if (!response.ok) {
            throw new Error('A error occurred while trying to send the message.');
        }

        const answerReader = response.body.getReader();
        let partialAnswer = '';

        while (true) {
            const {
                done: done,
                value: answerPiece
            } = await answerReader.read();

            if (done) break;

            partialAnswer += new TextDecoder().decode(answerPiece);
            newBubbleBot.innerHTML = marked.parse(partialAnswer);
            goToBottom();
        }
    }).catch(error => {
        alert(error.message);
    });
}

function createUserBubble() {
    const bolha = document.createElement('p');
    bolha.classList = 'bubble__chat chat__bolha--user';
    return bolha;
}

function createBubbleBot() {
    let bolha = document.createElement('p');
    bolha.classList = 'bubble__chat chat__bolha--bot';
    return bolha;
}

function goToBottom() {
    chat.scrollTop = chat.scrollHeight;
}
