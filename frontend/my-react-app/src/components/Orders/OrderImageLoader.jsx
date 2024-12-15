import React, { useEffect, useState } from 'react';
import axios from 'axios';

const ImageLoader = ({ orderId, alt, actualToken, isEditing, handleSaveOrderChanges }) => {
    const [image, setImage] = useState(null);

    const fetchImage = () => {
        axios.get(`api/warehouse/orders/oderImage/${orderId}`, {
            headers: {
                Authorization: `Bearer ${actualToken}`,
            },
            responseType: 'blob',
        })
            .then((data) => {
                const imageBlob = new Blob([data.data], { type: 'image/jpeg' });
                const imageUrl = URL.createObjectURL(imageBlob);
                setImage(imageUrl);
            })
            .catch((error) => {
                console.log('Error fetching image:', error);
            });
    };

    useEffect(() => {
        fetchImage();
    }, [orderId, actualToken]);

    const handleImageChange = (event) => {
        const imageFile = event.target.files[0];
        if (imageFile) {
            const imageUrl = URL.createObjectURL(imageFile);
            setImage(imageUrl);

            handleSaveOrderChanges(orderId, 'imageFile', imageFile);
            handleSaveOrderChanges(orderId, 'fileName', imageFile.name);
        }
    };

    return (
        <div>
            {image && (
                <img
                    className="product-image"
                    src={image}
                    alt={alt}
                    onClick={() => isEditing && document.getElementById(`file-input-${orderId}`).click()}
                />
            )}

            <input
                type="file"
                id={`file-input-${orderId}`}
                style={{ display: 'none' }}
                onChange={handleImageChange}
            />
        </div>
    );
};

export default ImageLoader;