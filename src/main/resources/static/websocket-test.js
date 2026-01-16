let stompClient = null;
let channelSubscriptions = {};
let notificationSubscription = null;

const UUID_REGEX = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

function isValidUUID(str) {
    return UUID_REGEX.test(str.trim());
}

function validateInput(inputId, shouldBeUUID = true) {
    const input = document.getElementById(inputId);
    const value = input.value.trim();

    if (!value) {
        input.classList.add('error');
        return null;
    }

    if (shouldBeUUID && !isValidUUID(value)) {
        input.classList.add('error');
        addMessage('Invalid UUID format: ' + value, 'error');
        return null;
    }

    input.classList.remove('error');
    return value;
}

function updateButtons(connected) {
    document.getElementById('connectBtn').disabled = connected;
    document.getElementById('disconnectBtn').disabled = !connected;
    document.getElementById('subscribeBtn').disabled = !connected;
    document.getElementById('notifySubBtn').disabled = !connected;
    document.getElementById('sendBtn').disabled = !connected;
}

function connect() {
    const socket = new SockJS('http://localhost:8080/api/ws');
    stompClient = Stomp.over(socket);
    stompClient.debug = null;

    stompClient.connect({}, function (frame) {
        const status = document.getElementById('connectionStatus');
        status.className = 'status-bar connected';
        status.innerHTML = '<span class="status-dot"></span><span>Connected</span>';
        updateButtons(true);
        addMessage('Connected to WebSocket server', 'system');
    }, function (error) {
        const status = document.getElementById('connectionStatus');
        status.className = 'status-bar';
        status.innerHTML = '<span class="status-dot"></span><span>Connection failed</span>';
        updateButtons(false);
        addMessage('Connection error: ' + error, 'error');
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
        stompClient = null;
    }
    channelSubscriptions = {};
    notificationSubscription = null;
    const status = document.getElementById('connectionStatus');
    status.className = 'status-bar';
    status.innerHTML = '<span class="status-dot"></span><span>Disconnected</span>';
    document.getElementById('channelSubsList').innerHTML = '<span class="empty">None</span>';
    document.getElementById('notifyStatusText').textContent = 'Not subscribed';
    document.getElementById('notifyStatusText').className = 'empty';
    document.getElementById('notifySubBtn').disabled = true;
    updateButtons(false);
    addMessage('Disconnected', 'system');
}

function subscribeChannel() {
    const channelId = validateInput('channelId', true);
    if (!channelId) return;

    if (channelSubscriptions[channelId]) {
        addMessage('Already subscribed to this channel', 'error');
        return;
    }

    const subscription = stompClient.subscribe('/topic/channel.' + channelId, function (message) {
        const msg = JSON.parse(message.body);
        showMessage(msg, channelId);
    });

    channelSubscriptions[channelId] = subscription;
    updateChannelSubsList();
    document.getElementById('channelId').value = '';
    document.getElementById('targetChannelId').value = channelId;
    addMessage('Subscribed: ' + channelId.substring(0, 8) + '...', 'system');
}

function unsubscribeChannel(channelId) {
    if (channelSubscriptions[channelId]) {
        channelSubscriptions[channelId].unsubscribe();
        delete channelSubscriptions[channelId];
        updateChannelSubsList();
        addMessage('Unsubscribed: ' + channelId.substring(0, 8) + '...', 'system');
    }
}

function updateChannelSubsList() {
    const list = document.getElementById('channelSubsList');
    const channels = Object.keys(channelSubscriptions);

    if (channels.length === 0) {
        list.innerHTML = '<span class="empty">None</span>';
        return;
    }

    list.innerHTML = channels.map(id => `
        <div class="sub-badge">
            <span title="${id}">${id.substring(0, 8)}...</span>
            <button class="btn-danger" onclick="unsubscribeChannel('${id}')">x</button>
        </div>
    `).join('');
}

function subscribeNotifications() {
    const userId = validateInput('senderId', true);
    if (!userId) return;

    if (notificationSubscription) {
        addMessage('Already subscribed to notifications', 'error');
        return;
    }

    notificationSubscription = stompClient.subscribe('/user/' + userId + '/queue/notifications', function (message) {
        const notification = JSON.parse(message.body);
        showNotification(notification);
    });

    document.getElementById('notifyStatusText').textContent = 'Subscribed';
    document.getElementById('notifySubBtn').textContent = 'Subscribed';
    document.getElementById('notifySubBtn').disabled = true;
    addMessage('Subscribed to notifications', 'system');
}

function sendMessage() {
    const channelId = validateInput('targetChannelId', true);
    const senderId = validateInput('senderId', true);
    const content = document.getElementById('messageContent').value.trim();

    if (!channelId || !senderId) return;

    if (!content) {
        document.getElementById('messageContent').classList.add('error');
        return;
    }
    document.getElementById('messageContent').classList.remove('error');

    try {
        stompClient.send('/app/chat.' + channelId, {}, JSON.stringify({
            channelId: channelId,
            content: content,
            senderId: senderId
        }));
        addMessage('Sent: ' + content, 'system');
        document.getElementById('messageContent').value = '';
    } catch (e) {
        addMessage('Send failed: ' + e.message, 'error');
    }
}

function handleEnter(event) {
    if (event.key === 'Enter') {
        sendMessage();
    }
}

function showMessage(msg, channelId) {
    const sender = msg.senderName || msg.senderId?.substring(0, 8) || 'Unknown';
    addMessage('[' + channelId.substring(0, 8) + '...] ' + sender + ': ' + msg.content);
}

function showNotification(notification) {
    addMessage('[notification] ' + notification.type + ': ' + (notification.message || 'New notification'), 'notification');
}

function addMessage(text, type = '') {
    const container = document.getElementById('messages');

    if (container.querySelector('.empty-state')) {
        container.innerHTML = '';
    }

    const div = document.createElement('div');
    div.className = 'message ' + type;
    div.innerHTML = '<span class="time">' + new Date().toLocaleTimeString() + '</span><div class="content">' + escapeHtml(text) + '</div>';
    container.prepend(div);

    if (container.children.length > 100) {
        container.removeChild(container.lastChild);
    }
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function clearMessages() {
    document.getElementById('messages').innerHTML = '<div class="empty">Cleared</div>';
}

document.querySelectorAll('input').forEach(input => {
    input.addEventListener('input', function () {
        this.classList.remove('error');
    });
});
