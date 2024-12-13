import React, {useState, useEffect} from 'react';
import './Warehouse.css';
import {getCookie} from "../../Token/Token"
import axios from "axios";
import ImageLoader from "./ImageLoader";
import EditableField from "./EditableField";
import {Link} from "react-router-dom";

const warehouses = ['Main Warehouse', 'Warehouse B', 'Warehouse C'];
const categories = ['Electronics', 'Clothing', 'Books', "Home Decor", "Sports & Outdoors"];

const Warehouse = () => {
    const [selectedWarehouse, setSelectedWarehouse] = useState('');
    const [selectedCategory, setSelectedCategory] = useState('');
    const [filter, setFilter] = useState('');
    const [products, setProducts] = useState([]);
    const [editingProductId, setEditingProductId] = useState(null);
    const [updatedProduct, setUpdatedProduct] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);

    const jwtToken = getCookie('jwtToken');
    const tokenObject = JSON.parse(jwtToken);
    const actualToken = tokenObject.token;

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await axios.get('api/warehouse/items', {
                    params: {
                        page: currentPage,
                        size: 10,
                        warehouse: selectedWarehouse,
                        category: selectedCategory,
                        filter: filter,
                    },
                    headers: {
                        Authorization: `Bearer ${actualToken}`,
                    },
                });

                if (response.status === 200) {
                    setProducts(response.data.content);
                    setTotalPages(response.data.totalPages);
                } else {
                    console.error('Request failed with status:', response.status);
                }
            } catch (error) {
                console.error('Error during fetch:', error);
            }
        };

        fetchData();
    }, [currentPage, selectedWarehouse, selectedCategory, filter]);

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

    const handleFilterChange = (event) => {
        setFilter(event.target.value);
        setCurrentPage(0);
    };

    const handleEditProduct = (productId) => {
        setEditingProductId(productId);
    };

    const handleSaveProductChanges = (productId, fieldName, newValue) => {
        const productIndex = products.findIndex(product => product.id === productId);
        if (productIndex !== -1) {
            let upProduct = products[productIndex];
            upProduct[fieldName] = newValue;
            setUpdatedProduct(upProduct);

            // Update goods on the page
            setProducts(prevProducts => {
                return prevProducts.map(product => {
                    if (product.id === productId) {
                        return {...product, [fieldName]: newValue};
                    }
                    return product;
                });
            });
        }
    };

    const handleSaveUpdatedProduct = (id) => {
        handleSubmit();
        setEditingProductId(null);
    }

    const handleSubmit = async () => {
        try {
            const formDataUpdateProduct = new FormData();
            formDataUpdateProduct.append('id', updatedProduct.id);
            formDataUpdateProduct.append('sku', updatedProduct.sku);
            formDataUpdateProduct.append('name', updatedProduct.name);
            formDataUpdateProduct.append('quantity', updatedProduct.quantity);
            formDataUpdateProduct.append('price', updatedProduct.price);
            formDataUpdateProduct.append('category', updatedProduct.category);
            formDataUpdateProduct.append('arrivalDate', updatedProduct.arrivalDate);
            formDataUpdateProduct.append('supplier', updatedProduct.supplier);
            formDataUpdateProduct.append('warehouse', updatedProduct.warehouse);
            formDataUpdateProduct.append('fileName', updatedProduct.fileName);
            formDataUpdateProduct.append('image', updatedProduct.imageFile);

            const response = await axios.post('api/warehouse/items/update', formDataUpdateProduct, {
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

    const handleDeleteProduct = async (productId) => {
        try {
            const response = await axios.delete(`api/warehouse/items/delete/${productId}`, {
                headers: {
                    Authorization: `Bearer ${actualToken}`
                }
            });

            if (response.status === 200) {
                console.log(response.data);
                setProducts(prevOrders => prevOrders.filter(product => product.id !== productId));
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
                <h1>Warehouse Management System</h1>
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
                    <Link to="/add-product">
                        <button className="add-product-button">Add Product</button>
                    </Link>
                </div>
            </header>

            <div className="product-grid">
                <div className="product-info">Image</div>
                <div className="product-info">Sku</div>
                <div className="product-info">Name</div>
                <div className="product-info">Quantity</div>
                <div className="product-info">Price (UAH)</div>
                <div className="product-info">Category</div>
                <div className="product-info">ArrivalDate</div>
                <div className="product-info">Warehouse</div>
                <div className="product-info">Supplier</div>
                <div className="product-info">LastUpdated</div>
            </div>

            <div className="product-container">
                {products.map(product => (
                    <div key={product.id} className="product-grid">
                        <ImageLoader
                            imageId={product.id}
                            alt={product.fileName}
                            actualToken={actualToken}
                            isEditing={editingProductId === product.id}
                            handleSaveProductChanges={handleSaveProductChanges}
                        />
                        {editingProductId === product.id ? (
                            <>
                                <EditableField
                                    value={product.sku}
                                    onChange={(newValue) => handleSaveProductChanges(product.id, 'sku', newValue)}
                                    className="product-info2"
                                />
                                <EditableField
                                    value={product.name}
                                    onChange={(newValue) => handleSaveProductChanges(product.id, 'name', newValue)}
                                    className="product-info2"
                                />
                                <EditableField
                                    value={product.quantity}
                                    onChange={(newValue) => handleSaveProductChanges(product.id, 'quantity', newValue)}
                                    className="product-info2"
                                />
                                <EditableField
                                    value={product.price}
                                    onChange={(newValue) => handleSaveProductChanges(product.id, 'price', newValue)}
                                    className="product-info2"
                                />
                                <select
                                    className="product-info2"
                                    value={product.category}
                                    onChange={(e) => handleSaveProductChanges(product.id, 'category', e.target.value)}
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
                                    value={product.arrivalDate.substring(0, 10)}
                                    onChange={(e) => handleSaveProductChanges(product.id, 'arrivalDate', e.target.value)}
                                    className="product-info2"
                                />
                                <select
                                    className="product-info2"
                                    value={product.warehouse}
                                    onChange={(e) => handleSaveProductChanges(product.id, 'warehouse', e.target.value)}
                                >
                                    <option value="">Select Warehouse</option>
                                    {warehouses.map((warehouse, index) => (
                                        <option key={index} value={warehouse}>
                                            {warehouse}
                                        </option>
                                    ))}
                                </select>
                                <EditableField
                                    value={product.supplier}
                                    onChange={(newValue) => handleSaveProductChanges(product.id, 'supplier', newValue)}
                                    className="product-info2"
                                />
                            </>
                        ) : (
                            <>
                                <div className="product-info2">{product.sku}</div>
                                <div className="product-info2">{product.name}</div>
                                <div className="product-info2">{product.quantity}</div>
                                <div className="product-info2">{product.price}</div>
                                <div className="product-info2">{product.category}</div>
                                <div className="product-info2">{product.arrivalDate}</div>
                                <div className="product-info2">{product.warehouse}</div>
                                <div className="product-info2">{product.supplier}</div>
                            </>
                        )}
                        <div className="product-info2">
                            {new Date(product.lastUpdated).toISOString().replace("T", " ").substr(0, 16)}
                        </div>

                        <div className="product-info2">
                            {editingProductId === product.id ? (
                                <>
                                    <button className="save-button" onClick={() => handleSaveUpdatedProduct()}>Save
                                    </button>
                                </>
                            ) : (
                                <button className="edit-button"
                                        onClick={() => handleEditProduct(product.id)}>Edit</button>
                            )}
                        </div>
                        <div className="product-info2">
                            <button className="delete-button" onClick={() => handleDeleteProduct(product.id)}>Delete
                            </button>
                        </div>
                    </div>
                ))}
            </div>
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

export default Warehouse;