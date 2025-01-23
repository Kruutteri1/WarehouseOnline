import React from 'react';
import ImageLoader from './ImageLoader';
import EditableField from './EditableField';

const ProductList = ({
                         products,
                         editingProductId,
                         handleSaveProductChanges,
                         handleEditProduct,
                         handleSaveUpdatedProduct,
                         handleDeleteProduct,
                         categories,
                         warehouses,
                         actualToken,
                     }) => {
    return (
        <div className="product-container">

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
                        {new Date(product.lastUpdated).toISOString().replace('T', ' ').substr(0, 16)}
                    </div>

                    <div className="product-info2">
                        {editingProductId === product.id ? (
                            <button className="save-button"
                                    onClick={() => handleSaveUpdatedProduct(product.id)}>Save</button>
                        ) : (
                            <button className="edit-button" onClick={() => handleEditProduct(product.id)}>Edit</button>
                        )}
                    </div>
                    <div className="product-info2">
                        <button className="delete-button" onClick={() => handleDeleteProduct(product.id)}>Delete
                        </button>
                    </div>
                </div>
            ))}
        </div>
    );
};

export default ProductList;
