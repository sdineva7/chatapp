import axios from "axios";

export async function deleteChannel(channelId, userId) {
  try {
    const response = await axios.delete(
      `http://localhost:8080/api/channels/${channelId}?userId=${userId}`
    );
    return response.data.data;
  } catch (error) {
    throw error;
  }
}
