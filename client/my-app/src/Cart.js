import React from 'react';
import './Cart.css';

const Cart = ({ cartItems, onUpdateQuantity, onRemoveItem }) => {
  const formatPrice = (price) => {
    return new Intl.NumberFormat('ko-KR', {
      style: 'currency',
      currency: 'KRW'
    }).format(price);
  };

  const calculateTotal = () => {
    return cartItems.reduce((total, item) => total + (item.price * item.quantity), 0);
  };

  const getTotalItems = () => {
    return cartItems.reduce((total, item) => total + item.quantity, 0);
  };

  if (cartItems.length === 0) {
    return (
      <div className="cart-container">
        <h2>장바구니</h2>
        <div className="empty-cart">
          <p>장바구니가 비어있습니다.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="cart-container">
      <h2>장바구니 ({getTotalItems()}개)</h2>
      
      <div className="cart-items">
        {cartItems.map((item) => (
          <div key={item.id} className="cart-item">
            <div className="item-image">
              {item.imageUrl && (
                <img 
                  src={item.imageUrl} 
                  alt={item.name}
                  onError={(e) => {
                    e.target.style.display = 'none';
                  }}
                />
              )}
            </div>
            
            <div className="item-info">
              <h3 className="item-name">{item.name}</h3>
              <div className="item-price">{formatPrice(item.price)}</div>
            </div>
            
            <div className="item-controls">
              <button 
                className="quantity-btn minus"
                onClick={() => onUpdateQuantity(item.id, item.quantity - 1)}
                disabled={item.quantity <= 1}
              >
                -
              </button>
              
              <span className="quantity">{item.quantity}</span>
              
              <button 
                className="quantity-btn plus"
                onClick={() => onUpdateQuantity(item.id, item.quantity + 1)}
                disabled={item.quantity >= item.stock}
              >
                +
              </button>
              
              <button 
                className="remove-btn"
                onClick={() => onRemoveItem(item.id)}
              >
                삭제
              </button>
            </div>
            
            <div className="item-total">
              {formatPrice(item.price * item.quantity)}
            </div>
          </div>
        ))}
      </div>
      
      <div className="cart-summary">
        <div className="total-section">
          <div className="total-items">
            총 상품 수량: {getTotalItems()}개
          </div>
          <div className="total-price">
            총 결제금액: {formatPrice(calculateTotal())}
          </div>
        </div>
        
        <button className="checkout-btn">
          주문하기
        </button>
      </div>
    </div>
  );
};

export default Cart;
