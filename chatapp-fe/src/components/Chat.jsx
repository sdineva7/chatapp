import React, { useState, useEffect } from "react";
import "./Chat.css";
import useAuth from "../hooks/useAuth";
import { sendMessage } from "../services/channels/sendMessage";
import { getChannelMessages } from "../services/channels/getChannelMessages";
import { deleteChannel } from "../services/channels/deleteChannel";
import { toast } from "react-toastify";
import { useNavigate } from "react-router";

const Chat = ({ channelId }) => {
  const { user, userChannels } = useAuth();
  const [message, setMessage] = useState("");
  const [messages, setMessages] = useState([
    { text: "Welcome to the channel!", sender: "System" },
    { text: "Hello everyone!", sender: "Alice" },
    { text: "Hi Alice!", sender: "Bob" },
  ]);
  const [channelDetails, setChannelDetails] = useState({});
  const [inviteEmail, setInviteEmail] = useState("");
  
  const navigate = useNavigate();

  const handleSendMessage = async (e) => {
    e.preventDefault();
    if (message.trim()) {
      const response = await sendMessage(channelId, user.id, message);
      setMessages((prevMessages) => [
        ...prevMessages,
        { text: response.content, sender: user.email },
      ]);
      setMessage("");
    }
  };

  const handleInviteUser = (e) => {
    e.preventDefault();
    if (inviteEmail.trim()) {
      // Logic to invite user
      alert(`Invite sent to ${inviteEmail}`);
      setInviteEmail("");
    }
  };

  const handleDeleteChannel = async () => {
    if (channelDetails?.roleId === 1) {
      await deleteChannel(channelId, user.id);
      toast.success("Channel deleted!");
      navigate("/dashboard");
    } else {
      toast.error("You don't have permission to delete this channel");
    }
  };

  useEffect(() => {
    if (userChannels) {
      const channel = userChannels.find(
        (channel) => channel.id === Number(channelId)
      );
    
      setChannelDetails(channel);
    }
  }, [channelId, userChannels]);

  useEffect(() => {
    if (channelId) {
      getChannelMessages(channelId).then((messages) => {
        setMessages(
          messages.map((msg) => ({ text: msg.content, sender: msg.user.email }))
        );
      });
    }
  }, [channelId]);

  return (
    <div className="chat-container">
      <div className="chat-header">
        <div className="channel-info">
          <span className="channel-name ">{channelDetails?.name}</span>
        </div>
        <form className="invite-form" onSubmit={handleInviteUser}>
          <input
            type="email"
            value={inviteEmail}
            onChange={(e) => setInviteEmail(e.target.value)}
            placeholder="Invite user by email"
          />
          <button type="submit">Invite</button>
        </form>
        {channelDetails?.roleId === 1 && (
          <button className="delete-button" onClick={handleDeleteChannel}>
            Delete Channel
          </button>
        )}
      </div>
      <div className="messages">
        {messages.map((msg, index) => (
          <div key={index} className="message">
            <strong>{msg.sender}: </strong>
            {msg.text}
          </div>
        ))}
      </div>
      <form className="message-form" onSubmit={handleSendMessage}>
        <input
          type="text"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          placeholder="Type a message"
        />
        <button type="submit">Send</button>
      </form>
    </div>
  );
};

export default Chat;
