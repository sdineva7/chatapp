import axios from "axios";

export async function createChannel(channelName, userId) {
  try {
    const response = await axios.post(
      `http://localhost:8080/api/channels?userId=${userId}`,
      {
        name: channelName,
      }
    );
    return response.data.data;
  } catch (error) {
    throw error;
  }
}
