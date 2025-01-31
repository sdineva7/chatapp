import axios from "axios";

export async function removeGuest(channelId, userId, guestId) {
  try {
    const response = await axios.delete(
      `http://localhost:8080/api/channels/${channelId}/remove-guest?ownerId=${userId}&guestId=${guestId}`
    );
    return response.data.data;
  } catch (error) {
    throw error;
  }
}
