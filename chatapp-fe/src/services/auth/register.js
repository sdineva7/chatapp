import axios from 'axios';

export async function register(email, username, password) {
    try {
        const response = await axios.post('http://localhost:8080/api/users/register', {
            email,
            password
        });
        return response.data;
    } catch (error) {
        throw error;
    }
}