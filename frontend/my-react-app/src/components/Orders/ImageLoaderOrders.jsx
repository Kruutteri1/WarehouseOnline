import React, {useEffect, useState} from 'react';
import axios from 'axios';

const ImageLoader = ({ imageId, alt, actualToken, onImageLoad }) => {
    const [image, setImage] = useState(null);
    const [response, setResponse] = useState(null);

    const fetchImage =  () => {

        axios.get(`api/warehouse/orders/oderImage/${imageId}`, {
            headers: {
                Authorization: `Bearer ${actualToken}`
            },
            responseType: "blob"
        }).then((data) => {
            setResponse(data);
        }).catch((error) => {console.log(error)});
    };
    useEffect(() => {
        if (response && response.status === 200) {
            const imageBlob = new Blob([response.data], { type: 'image/jpeg' });
            const imageUrl = URL.createObjectURL(imageBlob);
            setImage(imageUrl);
        }
    }, [response]);
    useEffect(() => {


        fetchImage();
    }, [imageId, actualToken]);

    return <img className="product-image" src={image} alt={alt} />;
};

export default ImageLoader;