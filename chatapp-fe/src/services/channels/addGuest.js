import axios from "axios";

export async function addGuest(channelId, userId, guestId) {
  try {
    const response = await axios.post(
      `http://localhost:8080/api/channels/${channelId}/add-guest?userId=${userId}&guestId=${guestId}`
    );
    return response.data.data;
  } catch (error) {
    throw error;
  }
}
