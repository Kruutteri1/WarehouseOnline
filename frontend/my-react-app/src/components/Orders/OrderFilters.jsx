import {Link} from "react-router-dom";
import React from "react";

const OrderFilters = ({
                          warehouses,
                          categories,
                          statuses,
                          filter,
                          selectedStatus,
                          selectedWarehouse,
                          selectedCategory,
                          handleWarehouseChange,
                          handleCategoryChange,
                          handleStatusChange,
                          handleFilterChange
                      }) => (
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

            <label>Status:</label>
            <select value={selectedStatus} onChange={handleStatusChange}>
                <option value="">Select Status</option>
                {statuses.map((status, index) => (
                    <option key={index} value={status}>
                        {status}
                    </option>
                ))}
            </select>

            <label>Filter:</label>
            <input type="text" value={filter} onChange={handleFilterChange}/>
            <Link to="/add-order">
                <button className="add-order-button">Add Order</button>
            </Link>
        </div>
    </header>
);

export default OrderFilters;