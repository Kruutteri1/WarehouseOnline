import React, {useState, useEffect} from 'react';
import './Warehouse.css';
import {getCookie} from "../../Token/Token"
import WarehouseFilters from "./WarehouseFilters";
import ProductList from "./ProductList";
import {deleteItem, getItems, updateItem} from "./WarehouseService";

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
    const actualToken = JSON.parse(jwtToken);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await getItems(actualToken, selectedWarehouse, selectedCategory, filter, currentPage);

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
            if (updatedProduct.imageFile) {
                formDataUpdateProduct.append('image', updatedProduct.imageFile);
            }

            const response = await updateItem(formDataUpdateProduct, actualToken);

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
            const response = await deleteItem(productId, actualToken);

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
            <WarehouseFilters
                warehouses={warehouses}
                categories={categories}
                selectedWarehouse={selectedWarehouse}
                selectedCategory={selectedCategory}
                filter={filter}
                onWarehouseChange={handleWarehouseChange}
                onCategoryChange={handleCategoryChange}
                onFilterChange={handleFilterChange}
            />

            <ProductList
                products={products}
                editingProductId={editingProductId}
                handleSaveProductChanges={handleSaveProductChanges}
                handleEditProduct={handleEditProduct}
                handleSaveUpdatedProduct={handleSaveUpdatedProduct}
                handleDeleteProduct={handleDeleteProduct}
                categories={categories}
                warehouses={warehouses}
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

export default Warehouse;