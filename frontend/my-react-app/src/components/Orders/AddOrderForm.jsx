import React, { useState } from 'react';
import axios from 'axios';
import { getCookie } from '../../Token/Token';

const AddOrderForm = ({ onSuccess }) => {
    const categories = ['Electronics', 'Clothing', 'Books', "Home Decor", "Sports & Outdoors"];
    const warehouses = ['Main Warehouse', 'Warehouse B', 'Warehouse C'];
    const statuses = ['Created', 'Processing', 'Shipped', 'Delivered', 'Cancelled'];

    const [formValues, setFormValues] = useState({
        order_id: '',
        name: '',
        quantity: '',
        total_amount: '',
        category: '',
        orderDate: '',
        order_status: '',
        warehouse: '',
        delivery_date: '',
        image_path: null
    });

    const handleChange = (event) => {
        const { name, value } = event.target;
        setFormValues({ ...formValues, [name]: value });
    };

    const handleImageChange = (event) => {
        const file = event.target.files[0];
        setFormValues({ ...formValues, image: file });
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        try {
            const jwtToken = getCookie('jwtToken');
            const tokenObject = JSON.parse(jwtToken);
            const actualToken = tokenObject.token;

            const formData = new FormData();
            formData.append('order_id', formValues.order_id);
            formData.append('name', formValues.name);
            formData.append('quantity', formValues.quantity);
            formData.append('total_amount', formValues.total_amount);
            formData.append('category', formValues.category);
            formData.append('orderDate', formValues.orderDate);
            formData.append('status', formValues.status);
            formData.append('warehouse', formValues.warehouse);
            formData.append('delivery_date', formValues.delivery_date);
            formData.append('order_status', formValues.order_status);
            formData.append('image', formValues.image);

            const response = await axios.post('http://localhost:5000/api/warehouse/orders/add', formData, {
                headers: {
                    Authorization: `Bearer ${actualToken}`,
                    'Content-Type': 'multipart/form-data' // Указываем тип контента как multipart/form-data
                },
            });

            if (response.status === 200) {
                onSuccess();
            } else {
                console.error('Request failed with status:', response.status);
            }
        } catch (error) {
            console.error('Error during fetch:', error);
        }
    };

    return (
        <div className="form">
            <h2>Add New Order</h2>
            <form onSubmit={handleSubmit}>
                <label>Order id:</label>
                <input className="product-info2" type="text" name="order_id" value={formValues.order_id} onChange={handleChange}/>

                <label>Name:</label>
                <input className="product-info2" type="text" name="name" value={formValues.name}
                       onChange={handleChange}/>

                <label>Quantity:</label>
                <input className="product-info2" type="number" name="quantity" value={formValues.quantity}
                       onChange={handleChange}/>

                <label>Total Amount (UAH):</label>
                <input className="product-info2" type="number" name="total_amount" value={formValues.total_amount}
                       onChange={handleChange}/>

                <label>Warehouse:</label>
                <select className="product-info2" name="warehouse" value={formValues.warehouse} onChange={handleChange}>
                    <option value="">Select Warehouse</option>
                    {warehouses.map((warehouse, index) => (
                        <option key={index} value={warehouse}>
                            {warehouse}
                        </option>
                    ))}
                </select>

                <label>Order Date:</label>
                <input className="product-info2" type="date" name="orderDate" value={formValues.orderDate}
                       onChange={handleChange}/>

                <label>Category:</label>
                <select className="product-info2" name="category" value={formValues.category} onChange={handleChange}>
                    <option value="">Select Category</option>
                    {categories.map((category, index) => (
                        <option key={index} value={category}>
                            {category}
                        </option>
                    ))}
                </select>

                <label>Delivery Date:</label>
                <input className="product-info2" type="date" name="delivery_date" value={formValues.delivery_date}
                       onChange={handleChange}/>

                <label>Status:</label>
                <select className="product-info2" name="order_status" value={formValues.order_status} onChange={handleChange}>
                    <option value="">Select Status</option>
                    {statuses.map((status, index) => (
                        <option key={index} value={status}>
                            {status}
                        </option>
                    ))}
                </select>

                <label>Image:</label>
                <input type="file" name="image" accept="image/*" onChange={handleImageChange}/>

                <button type="submit">Add Order</button>
            </form>
        </div>
    );
};

export default AddOrderForm;
