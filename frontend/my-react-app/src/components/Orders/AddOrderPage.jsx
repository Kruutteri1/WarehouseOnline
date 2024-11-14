import React from 'react';
import AddOrderForm from "./AddOrderForm";

const AddOrderPage = () => {
    return (
        <div className="add-order-page">
            <AddOrderForm onSuccess={() => alert("Product added successfully!")} />
        </div>
    );
};

export default AddOrderPage;
