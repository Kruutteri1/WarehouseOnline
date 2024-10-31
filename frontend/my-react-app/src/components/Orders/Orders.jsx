import React, { useState, useEffect } from 'react';
import './Orders.css';
import { getCookie } from "../../Token/Token"
import axios from "axios";
import AddOrderForm from "./AddOrderForm";
import EditableField from "../Warehouse/EditableField";
import ImageLoaderOrders from "./ImageLoaderOrders";

const warehouses = ['Main Warehouse', 'Warehouse B', 'Warehouse C'];
const categories = ['Electronics', 'Clothing', 'Books', "Home Decor", "Sports & Outdoors"];
const statuses = ['Created', 'Processing', 'Shipped', 'Delivered', 'Cancelled'];

const Order = () => {
    const [selectedWarehouse, setSelectedWarehouse] = useState('');
    const [selectedCategory, setSelectedCategory] = useState('');
    const [filter, setFilter] = useState('');
    const [orders, setOrders] = useState([]);
    const [showAddOrderForm, setShowAddOrderForm] = useState(false);
    const [image, setImage] = useState(null);
    const [editingOrderId, setEditingOrderId] = useState(null);
    const [updatedOrder, setUpdatedOrder] = useState([]); // Определение переменной updatedProducts


    const jwtToken = getCookie('jwtToken');
    const tokenObject = JSON.parse(jwtToken);
    const actualToken = tokenObject.token;

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await axios.get('api/warehouse/orders', {
                    params: {
                        warehouse: selectedWarehouse,
                        category: selectedCategory,
                        filter: filter,
                    },
                    headers: {
                        Authorization: `Bearer ${actualToken}`,
                    },
                });

                if (response.status === 200) {
                    setOrders(response.data);
                    console.log(orders);
                } else {
                    console.error('Request failed with status:', response.status);
                }
            } catch (error) {
                console.error('Error during fetch:', error);
            }
        };

        fetchData();
    }, [selectedWarehouse, selectedCategory, filter]);

    const handleImageLoad = (data) => {
        setImage(data);
    };

    const handleShowAddOrderForm = () => {
        setShowAddOrderForm(true);
    };

    const handleWarehouseChange = (event) => {
        setSelectedWarehouse(event.target.value);
    };

    const handleCategoryChange = (event) => {
        setSelectedCategory(event.target.value);
    };

    const handleFilterChange = (event) => {
        setFilter(event.target.value);
    };

    const handleEditOrder = (orderId) => {
        setEditingOrderId(orderId);
    };

    const handleSaveOrderChanges = (orderId, fieldName, newValue) => {
        const orderIndex = orders.findIndex(product => product.id === orderId);
        if (orderIndex !== -1) {
            let upOrder = orders[orderIndex];
            upOrder[fieldName] = newValue;
            setUpdatedOrder(upOrder);

            // Обновление товара на сайте
            setOrders(prevOrders => {
                return prevOrders.map(order => {
                    if (order.id === orderId) {
                        return {...order, [fieldName]: newValue};
                    }
                    return order;
                });
            });
        }
    };

    const handleSaveUpdatedOrder = () => {
        handleSubmit();
        setEditingOrderId(null);
    }

    const handleSubmit = async () => {
        try {
            const formDataUpdateOrder = new FormData();
            formDataUpdateOrder.append('id', updatedOrder.id);
            formDataUpdateOrder.append('order_id', updatedOrder.order_id);
            formDataUpdateOrder.append('name', updatedOrder.name);
            formDataUpdateOrder.append('quantity', updatedOrder.quantity);
            formDataUpdateOrder.append('total_amount', updatedOrder.total_amount);
            formDataUpdateOrder.append('category', updatedOrder.category);
            formDataUpdateOrder.append('orderDate', updatedOrder.orderDate);
            formDataUpdateOrder.append('warehouse', updatedOrder.warehouse);
            formDataUpdateOrder.append('delivery_date', updatedOrder.delivery_date);
            formDataUpdateOrder.append('order_status', updatedOrder.order_status);

            const response = await axios.post('http://localhost:5000/api/warehouse/orders/update', formDataUpdateOrder, {
                headers: {
                    Authorization: `Bearer ${actualToken}`,
                    'Content-Type': 'multipart/form-data'
                },
            });

            if (response.status === 200) {
                console.log(response.status);
            } else {
                console.error('Request failed with status:', response.status);
            }
        } catch (error) {
            console.error('Error during fetch:', error);
        }
    };

    const handleDeleteOrder= async (orderId) => {
        try {
            const response = await axios.delete(`api/warehouse/orders/delete/${orderId}`, {
                headers: {
                    Authorization: `Bearer ${actualToken}`
                }
            });

            if (response.status === 200) {
                console.log(`Order with ID ${orderId} has been successfully deleted.`);
                setOrders(prevOrders => prevOrders.filter(order => order.id !== orderId));
            } else {
                console.error('Request failed with status:', response.status);
            }
        } catch (error) {
            console.error('Error during fetch:', error);
        }
    };

    return (
        <div>
            <header>
                <h1>Warehouse Order Management System</h1>
                <div className="filter-section">
                    <label>Warehouse:</label>
                    <select value={selectedWarehouse} onChange={handleWarehouseChange}>
                        <option value="">Select Warehouse</option>
                        {warehouses.map((warehouse, index) => (
                            <option key={index} value={warehouse}>
                                {warehouse}
                            </option>
                        ))}
                    </select>

                    <label>Category:</label>
                    <select value={selectedCategory} onChange={handleCategoryChange}>
                        <option value="">Select Category</option>
                        {categories.map((category, index) => (
                            <option key={index} value={category}>
                                {category}
                            </option>
                        ))}
                    </select>

                    <label>Filter:</label>
                    <input type="text" value={filter} onChange={handleFilterChange}/>
                    <button onClick={handleShowAddOrderForm}>Add Order</button>
                </div>
            </header>

            <div className="order-grid">
                <div className="order-info">Image</div>
                <div className="order-info">Order id</div>
                <div className="order-info">Name</div>
                <div className="order-info">Quantity</div>
                <div className="order-info">Total Amount (UAH)</div>
                <div className="order-info">Category</div>
                <div className="order-info">OrderDate</div>
                <div className="order-info">Warehouse</div>
                <div className="order-info">Delivery Date</div>
                <div className="order-info">Status</div>
            </div>

            <div className="order-container">
                {showAddOrderForm && <AddOrderForm onSuccess={() => setShowAddOrderForm(false)}/>}
                {orders.map(order => (
                    <div key={order.id} className="order-grid">
                        <ImageLoaderOrders
                            imageId={order.id}
                            alt={order.fileName}
                            actualToken={actualToken}
                            onImageLoad={handleImageLoad}
                        />
                        {editingOrderId === order.id ? (
                            <>
                                <EditableField
                                    value={order.order_id}
                                    onChange={(newValue) => handleSaveOrderChanges(order.id, 'order_id', newValue)}
                                    className="order-info2"
                                />
                                <EditableField
                                    value={order.name}
                                    onChange={(newValue) => handleSaveOrderChanges(order.id, 'name', newValue)}
                                    className="order-info2"
                                />
                                <EditableField
                                    value={order.quantity}
                                    onChange={(newValue) => handleSaveOrderChanges(order.id, 'quantity', newValue)}
                                    className="order-info2"
                                />
                                <EditableField
                                    value={order.total_amount}
                                    onChange={(newValue) => handleSaveOrderChanges(order.id, 'total_amount', newValue)}
                                    className="order-info2"
                                />
                                <select
                                    className="order-info2"
                                    value={order.category}
                                    onChange={(e) => handleSaveOrderChanges(order.id, 'category', e.target.value)}
                                >
                                    <option value="">Select Category</option>
                                    {categories.map((category, index) => (
                                        <option key={index} value={category}>
                                            {category}
                                        </option>
                                    ))}
                                </select>
                                <input
                                    type="date"
                                    value={order.orderDate.substring(0, 10)}
                                    onChange={(e) => handleSaveOrderChanges(order.id, 'orderDate', e.target.value)}
                                    className="order-info2"
                                />
                                <select
                                    className="order-info2"
                                    value={order.warehouse}
                                    onChange={(e) => handleSaveOrderChanges(order.id, 'warehouse', e.target.value)}
                                >
                                    <option value="">Select Warehouse</option>
                                    {warehouses.map((warehouse, index) => (
                                        <option key={index} value={warehouse}>
                                            {warehouse}
                                        </option>
                                    ))}
                                </select>
                                <input
                                    type="date"
                                    value={order.delivery_date.substring(0, 10)}
                                    onChange={(e) => handleSaveOrderChanges(order.id, 'delivery_date', e.target.value)}
                                    className="order-info2"
                                />
                                <select
                                    className="order-info2"
                                    value={order.order_status}
                                    onChange={(e) => handleSaveOrderChanges(order.id, 'order_status', e.target.value)}
                                >
                                    <option value="">Select Status</option>
                                    {statuses.map((status, index) => (
                                        <option key={index} value={status}>
                                            {status}
                                        </option>
                                    ))}
                                </select>

                            </>
                        ) : (
                            <>
                                <div className="order-info2">{order.order_id}</div>
                                <div className="order-info2">{order.name}</div>
                                <div className="order-info2">{order.quantity}</div>
                                <div className="order-info2">{order.total_amount}</div>
                                <div className="order-info2">{order.category}</div>
                                <div className="order-info2">{order.orderDate}</div>
                                <div className="order-info2">{order.warehouse}</div>
                                <div className="order-info2">{order.delivery_date}</div>
                                <div className="order-info2">{order.order_status}</div>
                            </>
                        )}

                        <div className="order-info2">
                            {editingOrderId === order.id ? (
                                <>
                                    <button onClick={() => handleSaveUpdatedOrder()}>Save</button>
                                </>
                            ) : (
                                <button onClick={() => handleEditOrder(order.id)}>Edit</button>
                            )}
                        </div>
                        <button className="order-info2" onClick={() => handleDeleteOrder(order.id)}>Delete</button>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default Order;
