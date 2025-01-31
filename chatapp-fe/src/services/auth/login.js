import axios from 'axios';

export async function login(email, password) {
    try {
        const response = await axios.post('http://localhost:8080/api/users/login', {
            email,
            password
        });
        return response.data.data;
    } catch (error) {
        throw error;
    }
}