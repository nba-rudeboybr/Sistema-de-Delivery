// src/app/services/payment.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Payment {
  id?: number;
  orderId: number;
  amount: number;
  paymentMethod: 'CASH' | 'CREDIT_CARD' | 'DEBIT_CARD' | 'PIX' | 'BANK_TRANSFER';
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED' | 'REFUNDED';
  transactionId?: string;
  cardLastFour?: string;
  cashReceived?: number;
  changeAmount?: number;
  notes?: string;
  processedBy?: string;
  createdAt?: Date;
  processedAt?: Date;
}

export interface PaymentSummary {
  totalRevenue: number;
  completedPayments: number;
  revenueByMethod: any[];
}

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private apiUrl = 'http://localhost:8080/payments';

  constructor(private http: HttpClient) {}

  getPaymentsByOrderId(orderId: number): Observable<Payment[]> {
    return this.http.get<Payment[]>(`${this.apiUrl}/order/${orderId}`);
  }

  getCompletedPaymentsByOrderId(orderId: number): Observable<Payment[]> {
    return this.http.get<Payment[]>(`${this.apiUrl}/order/${orderId}/completed`);
  }

  getPaymentsByStatus(status: string): Observable<Payment[]> {
    return this.http.get<Payment[]>(`${this.apiUrl}/status/${status}`);
  }

  getPaymentsByMethod(method: string): Observable<Payment[]> {
    return this.http.get<Payment[]>(`${this.apiUrl}/method/${method}`);
  }

  getPayment(id: number): Observable<Payment> {
    return this.http.get<Payment>(`${this.apiUrl}/${id}`);
  }

  createPayment(orderId: number, amount: number, paymentMethod: string): Observable<Payment> {
    return this.http.post<Payment>(`${this.apiUrl}`, {
      orderId,
      amount,
      paymentMethod
    });
  }

  processCashPayment(id: number, cashReceived: number, processedBy: string): Observable<Payment> {
    return this.http.post<Payment>(`${this.apiUrl}/${id}/process-cash`, {
      cashReceived,
      processedBy
    });
  }

  processCardPayment(id: number, transactionId: string, cardLastFour: string, processedBy: string): Observable<Payment> {
    return this.http.post<Payment>(`${this.apiUrl}/${id}/process-card`, {
      transactionId,
      cardLastFour,
      processedBy
    });
  }

  processPixPayment(id: number, transactionId: string, processedBy: string): Observable<Payment> {
    return this.http.post<Payment>(`${this.apiUrl}/${id}/process-pix`, {
      transactionId,
      processedBy
    });
  }

  updatePaymentStatus(id: number, status: string): Observable<Payment> {
    return this.http.patch<Payment>(`${this.apiUrl}/${id}/status`, { status });
  }

  addPaymentNotes(id: number, notes: string): Observable<Payment> {
    return this.http.patch<Payment>(`${this.apiUrl}/${id}/notes`, { notes });
  }

  getRevenueByDateRange(startDate: Date, endDate: Date): Observable<PaymentSummary> {
    const params = {
      startDate: startDate.toISOString(),
      endDate: endDate.toISOString()
    };
    return this.http.get<PaymentSummary>(`${this.apiUrl}/revenue`, { params });
  }

  getPaymentsByProcessedBy(processedBy: string): Observable<Payment[]> {
    return this.http.get<Payment[]>(`${this.apiUrl}/processed-by/${processedBy}`);
  }

  isOrderFullyPaid(orderId: number): Observable<{fullyPaid: boolean}> {
    return this.http.get<{fullyPaid: boolean}>(`${this.apiUrl}/order/${orderId}/fully-paid`);
  }

  deletePayment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Helper methods
  getPaymentMethodText(method: string): string {
    switch (method) {
      case 'CASH': return 'Dinheiro';
      case 'CREDIT_CARD': return 'Cartão de Crédito';
      case 'DEBIT_CARD': return 'Cartão de Débito';
      case 'PIX': return 'PIX';
      case 'BANK_TRANSFER': return 'Transferência Bancária';
      default: return method;
    }
  }

  getPaymentStatusText(status: string): string {
    switch (status) {
      case 'PENDING': return 'Pendente';
      case 'PROCESSING': return 'Processando';
      case 'COMPLETED': return 'Concluído';
      case 'FAILED': return 'Falhou';
      case 'REFUNDED': return 'Reembolsado';
      default: return status;
    }
  }

  getPaymentStatusColor(status: string): string {
    switch (status) {
      case 'PENDING': return '#ffc107';
      case 'PROCESSING': return '#17a2b8';
      case 'COMPLETED': return '#28a745';
      case 'FAILED': return '#dc3545';
      case 'REFUNDED': return '#6c757d';
      default: return '#6c757d';
    }
  }

  getPaymentMethodIcon(method: string): string {
    switch (method) {
      case 'CASH': return '💰';
      case 'CREDIT_CARD': return '💳';
      case 'DEBIT_CARD': return '💳';
      case 'PIX': return '📱';
      case 'BANK_TRANSFER': return '🏦';
      default: return '💳';
    }
  }
}
