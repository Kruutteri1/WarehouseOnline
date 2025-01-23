import {Link} from "react-router-dom";

const WarehouseFilters = ({
                              warehouses,
                              categories,
                              selectedWarehouse,
                              selectedCategory,
                              filter,
                              onWarehouseChange,
                              onCategoryChange,
                              onFilterChange
                          }) => (
    <header>
        <h1>Warehouse Management System</h1>
        <div className="filter-section">
            <label>Warehouse:</label>
            <select value={selectedWarehouse} onChange={onWarehouseChange}>
                <option value="">Select Warehouse</option>
                {warehouses.map((warehouse, index) => (
                    <option key={index} value={warehouse}>
                        {warehouse}
                    </option>
                ))}
            </select>

            <label>Category:</label>
            <select value={selectedCategory} onChange={onCategoryChange}>
                <option value="">Select Category</option>
                {categories.map((category, index) => (
                    <option key={index} value={category}>
                        {category}
                    </option>
                ))}
            </select>

            <label>Filter:</label>
            <input type="text" value={filter} onChange={onFilterChange}/>
            <Link to="/add-product">
                <button className="add-product-button">Add Product</button>
            </Link>
        </div>
    </header>
);

export default WarehouseFilters;