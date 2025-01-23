import React from 'react';
import AddOrderForm from "./AddOrderForm";

const AddOrderPage = () => {
    return (
        <div className="add-order-page">
            <AddOrderForm onSuccess={() => alert("Order added successfully!")} />
        </div>
    );
};

export default AddOrderPage;
