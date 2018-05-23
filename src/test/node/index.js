const Stomp = require('stompjs');

const client = Stomp.overWS('ws://localhost:8080/notifications');
client.connect({}, frame => {
    console.log('Connected: ' + frame);
    client.subscribe('/DANMU_MSG_EVENT', message => {
        console.log('Received message: \n' + message.body)
    });
});
