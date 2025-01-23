import axios from 'axios';

const BASE_URL = 'api/warehouse/orders';

const getOrders = async (token, selectedWarehouse, selectedCategory, selectedStatus, filter, currentPage) => {
    return await axios.get(`${BASE_URL}`, {
        params: {
            page: currentPage,
            size: 5,
            warehouse: selectedWarehouse,
            category: selectedCategory,
            status: selectedStatus,
            filter: filter,
        },
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });
};

const updateOrder = async (formDataUpdateOrder, token) => {
    return await axios.post(`${BASE_URL}/update`, formDataUpdateOrder, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });
}

const addOrder = async (formData, token) => {
    return await axios.post(`${BASE_URL}/add`, formData, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });
}

const deleteOrder = async (orderId, token) => {
    return axios.delete(`${BASE_URL}/delete/${orderId}`, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });
};

export {getOrders, updateOrder, addOrder, deleteOrder};