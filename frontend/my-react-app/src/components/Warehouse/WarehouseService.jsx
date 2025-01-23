import axios from 'axios';

const BASE_URL = 'api/warehouse/items';

const getItems = async (token, selectedWarehouse, selectedCategory, filter, currentPage) => {
    return await axios.get(`${BASE_URL}`, {
        params: {
            page: currentPage,
            size: 5,
            warehouse: selectedWarehouse,
            category: selectedCategory,
            filter: filter,
        },
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });
};

const updateItem = async (formDataUpdateProduct, token) => {
    return await axios.post(`${BASE_URL}/update`, formDataUpdateProduct, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });
}

const addItem = async (formData, token) => {
    return await axios.post(`${BASE_URL}/add`, formData, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });
}

const deleteItem = async (productId, token) => {
    return axios.delete(`${BASE_URL}/delete/${productId}`, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });
};

export {getItems, updateItem, addItem, deleteItem};