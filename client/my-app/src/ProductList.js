import React, { useState, useEffect } from 'react';
import './ProductList.css';

const API_BASE_URL = 'http://localhost:8080';

const ProductList = ({ cartItems, onAddToCart, onUpdateQuantity }) => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      setLoading(true);
      const response = await fetch(`${API_BASE_URL}/api/products`);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const data = await response.json();
      setProducts(data);
      setError(null);
    } catch (err) {
      setError('상품 목록을 불러오는데 실패했습니다: ' + err.message);
      console.error('Error fetching products:', err);
    } finally {
      setLoading(false);
    }
  };

  const formatPrice = (price) => {
    return new Intl.NumberFormat('ko-KR', {
      style: 'currency',
      currency: 'KRW'
    }).format(price);
  };

  const getCartQuantity = (productId) => {
    const cartItem = cartItems.find(item => item.id === productId);
    return cartItem ? cartItem.quantity : 0;
  };

  const handleQuantityChange = (product, newQuantity) => {
    if (newQuantity === 0) {
      // 카트에서 제거
      onUpdateQuantity(product.id, 0);
    } else {
      // 카트에 추가하거나 수량 업데이트
      const currentQuantity = getCartQuantity(product.id);
      if (currentQuantity === 0) {
        onAddToCart(product);
      } else {
        onUpdateQuantity(product.id, newQuantity);
      }
    }
  };

  if (loading) {
    return (
      <div className="product-list-container">
        <div className="loading">상품 목록을 불러오는 중...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="product-list-container">
        <div className="error">
          <p>{error}</p>
          <button onClick={fetchProducts} className="retry-button">
            다시 시도
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="product-list-container">
      <h1>상품 목록</h1>
      
      {products.length === 0 ? (
        <div className="no-products">
          <p>등록된 상품이 없습니다.</p>
        </div>
      ) : (
        <div className="products-grid">
          {products.map((product) => (
            <div key={product.id} className="product-card">
              {product.imageUrl && (
                <div className="product-image">
                  <img 
                    src={product.imageUrl} 
                    alt={product.name}
                    onError={(e) => {
                      e.target.style.display = 'none';
                    }}
                  />
                </div>
              )}
              
              <div className="product-info">
                <h3 className="product-name">{product.name}</h3>
                
                {product.description && (
                  <p className="product-description">{product.description}</p>
                )}
                
                <div className="product-details">
                  <div className="product-price">
                    {formatPrice(product.price)}
                  </div>
                  
                  <div className="product-stock">
                    재고: {product.stock}개
                  </div>
                  
                  {product.category && (
                    <div className="product-category">
                      카테고리: {product.category}
                    </div>
                  )}
                  
                  {product.brand && (
                    <div className="product-brand">
                      브랜드: {product.brand}
                    </div>
                  )}
                </div>
                
                <div className="product-status">
                  {product.isActive ? (
                    <span className="status-active">판매중</span>
                  ) : (
                    <span className="status-inactive">판매중단</span>
                  )}
                </div>
                
                {product.isActive && (
                  <div className="product-cart-controls">
                    <div className="quantity-controls">
                      <button 
                        className="quantity-btn minus"
                        onClick={() => handleQuantityChange(product, getCartQuantity(product.id) - 1)}
                        disabled={getCartQuantity(product.id) <= 0}
                      >
                        -
                      </button>
                      
                      <span className="quantity-display">
                        {getCartQuantity(product.id)}
                      </span>
                      
                      <button 
                        className="quantity-btn plus"
                        onClick={() => handleQuantityChange(product, getCartQuantity(product.id) + 1)}
                        disabled={getCartQuantity(product.id) >= product.stock}
                      >
                        +
                      </button>
                    </div>
                    
                    {getCartQuantity(product.id) > 0 && (
                      <div className="cart-status">
                        장바구니에 {getCartQuantity(product.id)}개 담김
                      </div>
                    )}
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default ProductList;
