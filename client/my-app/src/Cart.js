import React, { useState } from 'react';
import './Cart.css';

const API_BASE_URL = 'http://localhost:8080';

const Cart = ({ cartItems, onUpdateQuantity, onRemoveItem }) => {
  const [calculationResult, setCalculationResult] = useState(null);
  const [isCalculating, setIsCalculating] = useState(false);
  const [calculationError, setCalculationError] = useState(null);
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

  const calculateWithPromotions = async () => {
    if (cartItems.length === 0) {
      setCalculationError('장바구니가 비어있습니다.');
      return;
    }

    setIsCalculating(true);
    setCalculationError(null);
    setCalculationResult(null);

    try {
      const requestBody = {
        userId: 1, // 임시로 사용자 ID 1 사용
        items: cartItems.map(item => ({
          productId: item.id,
          quantity: item.quantity
        }))
      };

      const response = await fetch(`${API_BASE_URL}/api/cart/calculate-with-promotions`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestBody)
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const result = await response.json();
      setCalculationResult(result);
    } catch (err) {
      setCalculationError('가격 계산 중 오류가 발생했습니다: ' + err.message);
      console.error('Error calculating cart:', err);
    } finally {
      setIsCalculating(false);
    }
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
        
        <button 
          className="calculate-btn"
          onClick={calculateWithPromotions}
          disabled={isCalculating || cartItems.length === 0}
        >
          {isCalculating ? '계산 중...' : '프로모션 적용 가격 계산'}
        </button>
        
        {calculationError && (
          <div className="calculation-error">
            {calculationError}
          </div>
        )}
        
        {calculationResult && (
          <div className="calculation-result">
            <h3>프로모션 적용 계산 결과</h3>
            
            <div className="calculation-details">
              <div className="calculation-item">
                <span>소계:</span>
                <span>{formatPrice(calculationResult.subtotal)}</span>
              </div>
              
              {calculationResult.totalDiscount > 0 && (
                <div className="calculation-item discount">
                  <span>총 할인:</span>
                  <span>-{formatPrice(calculationResult.totalDiscount)}</span>
                </div>
              )}
              
              {calculationResult.shippingFee > 0 && (
                <div className="calculation-item">
                  <span>배송비:</span>
                  <span>{formatPrice(calculationResult.shippingFee)}</span>
                </div>
              )}
              
              <div className="calculation-item final">
                <span>최종 결제금액:</span>
                <span>{formatPrice(calculationResult.finalAmount)}</span>
              </div>
            </div>
            
            {calculationResult.appliedPromotions && calculationResult.appliedPromotions.length > 0 && (
              <div className="applied-promotions">
                <h4>적용된 프로모션</h4>
                {calculationResult.appliedPromotions.map((promotion, index) => (
                  <div key={index} className="promotion-item">
                    <span className="promotion-name">{promotion.promotionName}</span>
                    <span className="promotion-discount">-{formatPrice(promotion.discountAmount)}</span>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
        
        <button className="checkout-btn">
          주문하기
        </button>
      </div>
    </div>
  );
};

export default Cart;
