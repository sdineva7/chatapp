import React, { useState } from "react";
import "./CreateChannel.css";
import { createChannel } from "../../../services/channels/createChannel";
import useAuth from "../../../hooks/useAuth";
import { toast } from "react-toastify";
import { useNavigate } from "react-router";

const CreateChannel = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [channelName, setChannelName] = useState("");

  const handleCreateChannel = async (e) => {
    e.preventDefault();
    if (channelName.trim()) {
      // Logic to create channel
      const response = await createChannel(channelName, user.id);
      toast.success("Channel created successfully");
      navigate(`/channels/${response.id}`);
    }
  };

  return (
    <div className="create-channel-container">
      <h2>Create a New Channel</h2>
      <form className="create-channel-form" onSubmit={handleCreateChannel}>
        <label htmlFor="channelName">Channel Name:</label>
        <input
          type="text"
          id="channelName"
          value={channelName}
          onChange={(e) => setChannelName(e.target.value)}
          required
        />
        <button type="submit">Create Channel</button>
      </form>
    </div>
  );
};

export default CreateChannel;
