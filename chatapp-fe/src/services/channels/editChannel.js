import axios from "axios";

export async function editChannel(channelId, userId, name) {
  try {
    const response = await axios.put(
      `http://localhost:8080/api/channels?ownerId=${userId}`,
      {
        name: name,
        id: channelId,
      }
    );
    return response.data.data;
  } catch (err) {
    throw err;
  }
}
