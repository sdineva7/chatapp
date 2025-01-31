import axios from "axios";

export async function getChannelMessages(channelId) {
  try {
    const response = await axios.get(
      `http://localhost:8080/api/messages/channels/${channelId}/messages`
    );
    return response.data.data;
  } catch (error) {
    throw error;
  }
}
