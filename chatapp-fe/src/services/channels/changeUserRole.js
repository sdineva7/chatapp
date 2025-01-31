import axios from "axios";

export async function changeUserRole(channelId, userId, role) {
  try {
    const response = await axios.put(
      `http://localhost:8080/api/channels/${channelId}/promote-to-admin?ownerId=${userId}&guestId=${role}`
    );
    return response.data.data;
  } catch (error) {
    throw error;
  }
}
