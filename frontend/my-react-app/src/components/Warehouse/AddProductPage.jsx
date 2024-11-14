import React from 'react';
import AddProductForm from './AddProductForm';

const AddProductPage = () => {
    return (
        <div className="add-product-page">
            <AddProductForm onSuccess={() => alert("Product added successfully!")} />
        </div>
    );
};

export default AddProductPage;
