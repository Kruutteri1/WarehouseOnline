import React from 'react';
import OrderImageLoader from './OrderImageLoader';
import EditableField from '../Warehouse/EditableField';

const OrderList = ({
                       orders,
                       editingOrderId,
                       handleSaveOrderChanges,
                       handleEditOrder,
                       handleSaveUpdatedOrder,
                       handleDeleteOrder,
                       categories,
                       warehouses,
                       statuses,
                       actualToken,
                   }) => {
    return (
        <>
            {/* Заголовок таблицы */}
            <div className="order-grid">
                <div className="order-info">Image</div>
                <div className="order-info">Order ID</div>
                <div className="order-info">Name</div>
                <div className="order-info">Quantity</div>
                <div className="order-info">Total Amount (UAH)</div>
                <div className="order-info">Category</div>
                <div className="order-info">Order Date</div>
                <div className="order-info">Warehouse</div>
                <div className="order-info">Delivery Date</div>
                <div className="order-info">Status</div>
                <div className="order-info">Actions</div>
            </div>

            {/* Список заказов */}
            <div className="order-container">
                {orders.map((order) => (
                    <div key={order.id} className="order-grid">
                        {/* Загрузка изображения */}
                        <OrderImageLoader
                            orderId={order.id}
                            alt={order.fileName}
                            actualToken={actualToken}
                            isEditing={editingOrderId === order.id}
                            handleSaveOrderChanges={handleSaveOrderChanges}
                        />

                        {editingOrderId === order.id ? (
                            // Режим редактирования
                            <>
                                <EditableField
                                    value={order.orderId}
                                    onChange={(newValue) => handleSaveOrderChanges(order.id, 'orderId', newValue)}
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
                                    value={order.totalAmount}
                                    onChange={(newValue) => handleSaveOrderChanges(order.id, 'totalAmount', newValue)}
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
                                    value={order.deliveryDate.substring(0, 10)}
                                    onChange={(e) => handleSaveOrderChanges(order.id, 'deliveryDate', e.target.value)}
                                    className="order-info2"
                                />
                                <select
                                    className="order-info2"
                                    value={order.orderStatus}
                                    onChange={(e) => handleSaveOrderChanges(order.id, 'orderStatus', e.target.value)}
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
                            // Режим просмотра
                            <>
                                <div className="order-info2">{order.orderId}</div>
                                <div className="order-info2">{order.name}</div>
                                <div className="order-info2">{order.quantity}</div>
                                <div className="order-info2">{order.totalAmount}</div>
                                <div className="order-info2">{order.category}</div>
                                <div className="order-info2">{order.orderDate}</div>
                                <div className="order-info2">{order.warehouse}</div>
                                <div className="order-info2">{order.deliveryDate}</div>
                                <div className="order-info2">{order.orderStatus}</div>
                            </>
                        )}

                        {/* Кнопки действий */}
                        <div className="order-info2">
                            {editingOrderId === order.id ? (
                                <button className="save-button" onClick={handleSaveUpdatedOrder}>
                                    Save
                                </button>
                            ) : (
                                <button className="edit-button" onClick={() => handleEditOrder(order.id)}>
                                    Edit
                                </button>
                            )}
                        </div>
                        <div className="order-info2">
                            <button className="delete-button" onClick={() => handleDeleteOrder(order.id)}>
                                Delete
                            </button>
                        </div>
                    </div>
                ))}
            </div>
        </>
    );
};

export default OrderList;
