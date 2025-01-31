import axios from 'axios';

export async function getUserChannels(userId) {
    try {
        const response = await axios.get(`http://localhost:8080/api/channels/${userId}`);
        return response.data.data;
    } catch(err) {
        throw err;
    }
}