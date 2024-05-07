import React, { useState } from 'react';
import axios from 'axios';
import { getCookie } from '../../Token/Token';

const AddProductForm = ({ onSuccess }) => {
    const categories = ['Electronics', 'Clothing', 'Books', "Home Decor", "Sports & Outdoors"];
    const warehouses = ['Main Warehouse', 'Warehouse B', 'Warehouse C']; // Add your warehouse options

    const [formValues, setFormValues] = useState({
        sku: '',
        name: '',
        quantity: '',
        price: '',
        category: '',
        arrivalDate: '',
        supplier: '',
        warehouse: '',
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
            formData.append('sku', formValues.sku);
            formData.append('name', formValues.name);
            formData.append('quantity', formValues.quantity);
            formData.append('price', formValues.price);
            formData.append('category', formValues.category);
            formData.append('arrivalDate', formValues.arrivalDate);
            formData.append('supplier', formValues.supplier);
            formData.append('warehouse', formValues.warehouse);
            formData.append('image', formValues.image);

            const response = await axios.post('api/warehouse/items/add', formData, {
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
            <h2>Add New Product</h2>
            <form onSubmit={handleSubmit}>
                <label>SKU:</label>
                <input className="product-info2" type="text" name="sku" value={formValues.sku} onChange={handleChange}/>

                <label>Name:</label>
                <input className="product-info2" type="text" name="name" value={formValues.name}
                       onChange={handleChange}/>

                <label>Quantity:</label>
                <input className="product-info2" type="number" name="quantity" value={formValues.quantity}
                       onChange={handleChange}/>

                <label>Price (UAH):</label>
                <input className="product-info2" type="number" name="price" value={formValues.price}
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

                <label>Category:</label>
                <select className="product-info2" name="category" value={formValues.category} onChange={handleChange}>
                    <option value="">Select Category</option>
                    {categories.map((category, index) => (
                        <option key={index} value={category}>
                            {category}
                        </option>
                    ))}
                </select>

                <label>Arrival Date:</label>
                <input className="product-info2" type="date" name="arrivalDate" value={formValues.arrivalDate}
                       onChange={handleChange}/>

                <label>Supplier:</label>
                <input className="product-info2" type="text" name="supplier" value={formValues.supplier}
                       onChange={handleChange}/>

                <label>Image:</label>
                <input type="file" name="image" accept="image/*" onChange={handleImageChange}/>

                <button type="submit">Add Product</button>
            </form>
        </div>
    );
};

export default AddProductForm;
