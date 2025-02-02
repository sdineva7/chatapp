import React, { useState, useEffect } from "react";
import "./Chat.css";
import useAuth from "../hooks/useAuth";
import { sendMessage } from "../services/channels/sendMessage";
import { getChannelMessages } from "../services/channels/getChannelMessages";
import { deleteChannel } from "../services/channels/deleteChannel";
import { toast } from "react-toastify";
import { useNavigate } from "react-router";
import axios from 'axios';

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
  const [showMembersPopup, setShowMembersPopup] = useState(false);
  const [members, setMembers] = useState([]);

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

  const handleInviteUser = async (e) => {
    e.preventDefault();
    if (inviteEmail.trim()) {
      try {
        const response = await axios.get(`http://localhost:8080/api/users?search=${inviteEmail}`);
        if (response.data.data.length > 0) {
          const guestId = response.data.data[0].id;
          await axios.post(`http://localhost:8080/api/channels/${channelId}/add-guest?userId=${user.id}&guestId=${guestId}`);
          alert('Guest added to channel successfully!');
        } else {
          alert('User not found');
        }
      } catch (error) {
        console.error('Error inviting user:', error);
      }
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

  const handleToggleMembersPopup = async () => {
    setShowMembersPopup(!showMembersPopup);
    if (!showMembersPopup) {
      try {
        const response = await axios.get(`http://localhost:8080/api/channels/${channelId}/members`);
        setMembers(response.data);
      } catch (error) {
        console.error('Error fetching members:', error);
      }
    }
  };

  const handleToggleAdminRole = async (memberId, isAdmin) => {
    try {
      await axios.post(`http://localhost:8080/api/channels/${channelId}/toggle-admin`, { userId: memberId, isAdmin });
      alert('User role updated successfully!');
      handleToggleMembersPopup(); // Refresh members list
    } catch (error) {
      console.error('Error updating user role:', error);
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
          <>
            <button className="delete-button" onClick={handleDeleteChannel}>
              Delete Channel
            </button>
            <button className="members-button" onClick={handleToggleMembersPopup}>
              View Members
            </button>
          </>
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
      {showMembersPopup && (
        <div className="members-popup">
          <h3>Channel Members</h3>
          <ul>
            {members.map((member) => (
              <li key={member.id}>
                {member.name} ({member.email})
                <button onClick={() => handleToggleAdminRole(member.id, !member.isAdmin)}>
                  {member.isAdmin ? 'Remove Admin' : 'Make Admin'}
                </button>
              </li>
            ))}
          </ul>
          <button onClick={handleToggleMembersPopup}>Close</button>
        </div>
      )}
    </div>
  );
};

export default Chat;