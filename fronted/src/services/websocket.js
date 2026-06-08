import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

class WebSocketService {
  constructor() {
    this.stompClient = null;
    this.callbacks = {};
  }

  connect(userId) {
    return new Promise((resolve, reject) => {
      const socket = new SockJS('/ws');
      this.stompClient = Stomp.over(socket);
      const token = localStorage.getItem('accessToken');
      const headers = token ? { Authorization: `Bearer ${token}` } : {};

      this.stompClient.connect(headers, (frame) => {
        console.log('WebSocket connected:', frame);
        
        this.stompClient.subscribe('/user/queue/notifications', (message) => {
          const data = JSON.parse(message.body);
          this.trigger('notification', data);
        });

        this.stompClient.subscribe('/user/queue/messages', (message) => {
          const data = JSON.parse(message.body);
          this.trigger('message', data);
        });

        this.stompClient.subscribe('/topic/broadcast', (message) => {
          const data = JSON.parse(message.body);
          this.trigger('broadcast', data);
        });

        resolve(frame);
      }, (error) => {
        console.error('WebSocket connection error:', error);
        reject(error);
      });
    });
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.disconnect();
      this.stompClient = null;
    }
  }

  on(event, callback) {
    if (!this.callbacks[event]) {
      this.callbacks[event] = [];
    }
    this.callbacks[event].push(callback);
  }

  off(event, callback) {
    if (this.callbacks[event]) {
      this.callbacks[event] = this.callbacks[event].filter(cb => cb !== callback);
    }
  }

  trigger(event, data) {
    if (this.callbacks[event]) {
      this.callbacks[event].forEach(callback => callback(data));
    }
  }

  sendMessage(destination, message) {
    if (this.stompClient) {
      this.stompClient.send(destination, {}, JSON.stringify(message));
    }
  }
}

export const webSocketService = new WebSocketService();
