import axios from "axios";

export async function sendMessage(channelId, userId, message) {
  try {
    const response = await axios.post(
      `http://localhost:8080/api/messages`,
      {
        content: message,
        timestamp: new Date().toISOString(),
        channelId: Number(channelId),
        userId: userId,
      }
    );
    return response.data.data;
  } catch (error) {
    throw error;
  }
}
