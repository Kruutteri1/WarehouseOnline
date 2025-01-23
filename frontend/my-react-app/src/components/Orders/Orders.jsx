import React, { useState, useEffect } from 'react';
import './Orders.css';
import { getCookie } from "../../Token/Token"
import OrderFilters from "./OrderFilters";
import OrderList from "./OrderList";
import {deleteOrder, getOrders, updateOrder} from "./OrderService";

const warehouses = ['Main Warehouse', 'Warehouse B', 'Warehouse C'];
const categories = ['Electronics', 'Clothing', 'Books', "Home Decor", "Sports & Outdoors"];
const statuses = ['Created', 'Processing', 'Shipped', 'Delivered', 'Cancelled'];

const Order = () => {
    const [selectedWarehouse, setSelectedWarehouse] = useState('');
    const [selectedCategory, setSelectedCategory] = useState('');
    const [selectedStatus, setSelectedStatus] = useState('');
    const [filter, setFilter] = useState('');
    const [orders, setOrders] = useState([]);
    const [editingOrderId, setEditingOrderId] = useState(null);
    const [updatedOrder, setUpdatedOrder] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);

    const jwtToken = getCookie('jwtToken');
    const actualToken = JSON.parse(jwtToken);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await getOrders(actualToken, selectedWarehouse, selectedCategory, selectedStatus, filter, currentPage);

                if (response.status === 200) {
                    setOrders(response.data.content);
                    setTotalPages(response.data.totalPages);
                } else {
                    console.error('Request failed with status:', response.status);
                }
            } catch (error) {
                console.error('Error during fetch:', error);
            }
        };

        fetchData();
    }, [currentPage, selectedWarehouse, selectedCategory, selectedStatus, filter]);

    const handleNextPage = () => {
        if (currentPage < totalPages - 1) {
            setCurrentPage(prevPage => prevPage + 1);
        }
    };

    const handlePreviousPage = () => {
        if (currentPage > 0) {
            setCurrentPage(prevPage => prevPage - 1);
        }
    };

    const handleWarehouseChange = (event) => {
        setSelectedWarehouse(event.target.value);
        setCurrentPage(0);
    };

    const handleCategoryChange = (event) => {
        setSelectedCategory(event.target.value);
        setCurrentPage(0);
    };

    const handleStatusChange = (event) => {
        setSelectedStatus(event.target.value);
        setCurrentPage(0);
    };

    const handleFilterChange = (event) => {
        setFilter(event.target.value);
        setCurrentPage(0);
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
            formDataUpdateOrder.append('orderId', updatedOrder.orderId);
            formDataUpdateOrder.append('name', updatedOrder.name);
            formDataUpdateOrder.append('quantity', updatedOrder.quantity);
            formDataUpdateOrder.append('totalAmount', updatedOrder.totalAmount);
            formDataUpdateOrder.append('category', updatedOrder.category);
            formDataUpdateOrder.append('orderDate', updatedOrder.orderDate);
            formDataUpdateOrder.append('warehouse', updatedOrder.warehouse);
            formDataUpdateOrder.append('deliveryDate', updatedOrder.deliveryDate);
            formDataUpdateOrder.append('orderStatus', updatedOrder.orderStatus);
            formDataUpdateOrder.append('fileName', updatedOrder.fileName);
            if (updatedOrder.imageFile) {
                formDataUpdateOrder.append('image', updatedOrder.imageFile);
            }

            const response = await updateOrder(formDataUpdateOrder, actualToken);

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
            const response = await deleteOrder(orderId, actualToken);

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
            <OrderFilters
                warehouses={warehouses}
                categories={categories}
                statuses={statuses}
                selectedWarehouse={selectedWarehouse}
                selectedCategory={selectedCategory}
                selectedStatus={selectedStatus}
                filter={filter}
                handleWarehouseChange={handleWarehouseChange}
                handleCategoryChange={handleCategoryChange}
                handleStatusChange={handleStatusChange}
                handleFilterChange={handleFilterChange}
            />

            <OrderList
                orders={orders}
                editingOrderId={editingOrderId}
                handleEditOrder={handleEditOrder}
                handleSaveOrderChanges={handleSaveOrderChanges}
                handleSaveUpdatedOrder={handleSaveUpdatedOrder}
                handleDeleteOrder={handleDeleteOrder}
                categories={categories}
                warehouses={warehouses}
                statuses={statuses}
                actualToken={actualToken}
            />

            <div className="pagination">
                <button onClick={handlePreviousPage} disabled={currentPage === 0}>
                    Previous
                </button>
                <span>Page {currentPage + 1} of {totalPages}</span>
                <button onClick={handleNextPage} disabled={currentPage === totalPages - 1}>
                    Next
                </button>
            </div>
        </div>
    );
};

export default Order;