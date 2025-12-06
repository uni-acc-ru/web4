import { Component, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { Point } from './models/point.model';
import { User } from './models/user.model';
import { AuthService } from './services/auth.service';
import { PointService } from './services/point.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'Point Checker';
  
  x: number | null = null;
  y: number | null = null;
  r: number | null = 3;
  
  xValues = [-3, -2, -1, 0, 1, 2, 3, 4, 5];
  xOptions = [
    {label: '-3', value: -3},
    {label: '-2', value: -2},
    {label: '-1', value: -1},
    {label: '0', value: 0},
    {label: '1', value: 1},
    {label: '2', value: 2},
    {label: '3', value: 3},
    {label: '4', value: 4},
    {label: '5', value: 5}
  ];
  
  rValues = [1, 2, 3, 4, 5];
  rOptions = [
    {label: '1', value: 1},
    {label: '2', value: 2},
    {label: '3', value: 3},
    {label: '4', value: 4},
    {label: '5', value: 5}
  ];
  
  points: Point[] = [];
  
  currentUser: User | null = null;
  isLoggedIn = false;
  
  showLoginForm = false;
  showRegisterForm = false;
  loginUsername = '';
  loginPassword = '';
  registerUsername = '';
  registerPassword = '';
  loginError = '';
  registerError = '';
  
  showSuccessAnimation = false;
  randomImage: string = '';
  
  constructor(
    private authService: AuthService,
    private pointService: PointService,
    private messageService: MessageService
  ) {}
  
  ngOnInit() {
    this.isLoggedIn = this.authService.isLoggedIn();
    this.currentUser = this.authService.getCurrentUser();
    
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.isLoggedIn = !!user;
    });
    
    if (this.isLoggedIn) {
      this.loadPoints();
    }
    
    setTimeout(() => {
      this.redrawCanvas();
    }, 100);
  }
  
  login() {
    this.loginError = '';
    
    if (!this.loginUsername || !this.loginPassword) {
      this.loginError = 'Заполните все поля';
      return;
    }
    
    if (this.loginUsername.length < 4) {
      this.loginError = 'Имя пользователя должно быть минимум 4 символа';
      return;
    }
    
    if (this.loginPassword.length < 4) {
      this.loginError = 'Пароль должен быть минимум 4 символа';
      return;
    }
    
    this.authService.login({
      username: this.loginUsername,
      password: this.loginPassword
    }).subscribe({
      next: () => {
        this.showLoginForm = false;
        this.loginError = '';
        this.loginUsername = '';
        this.loginPassword = '';
        this.loadPoints();
        setTimeout(() => this.redrawCanvas(), 200);
      },
      error: () => {
        this.loginError = 'Неверное имя пользователя или пароль';
      }
    });
  }
  
  register() {
    this.registerError = '';
    
    if (!this.registerUsername || !this.registerPassword) {
      this.registerError = 'Заполните все поля';
      return;
    }
    
    if (this.registerUsername.length < 4) {
      this.registerError = 'Имя пользователя должно быть минимум 4 символа';
      return;
    }
    
    if (this.registerPassword.length < 4) {
      this.registerError = 'Пароль должен быть минимум 4 символа';
      return;
    }
    
    this.authService.register({
      username: this.registerUsername,
      password: this.registerPassword
    }).subscribe({
      next: () => {
        this.showRegisterForm = false;
        this.registerError = '';
        this.registerUsername = '';
        this.registerPassword = '';
        setTimeout(() => this.redrawCanvas(), 200);
      },
      error: (error) => {
        if (error.error?.message) {
          this.registerError = error.error.message;
        } else if (error.error?.error) {
          this.registerError = error.error.error;
        } else if (error.message) {
          this.registerError = error.message;
        } else {
          this.registerError = 'Ошибка регистрации. Проверьте данные и попробуйте снова';
        }
      }
    });
  }
  
  selectX(value: number) {
    this.x = value;
  }
  
  logout() {
    this.authService.logout();
    this.points = [];
  }
  
  checkPoint() {
    if (this.x === null || this.y === null || this.r === null) {
      this.messageService.add({severity: 'warn', summary: 'Предупреждение', detail: 'Заполните все поля'});
      return;
    }
    
    this.sendPointRequest(this.x, this.y, this.r);
  }
  
  private sendPointRequest(x: number, y: number, r: number) {
    this.pointService.checkPoint({ x, y, r }).subscribe({
      next: (point) => {
        this.points.unshift(point);
        this.drawPoint(point);
        
        if (point.hit) {
          const images = ['2.png', '3.png', '4.png', '5.png'];
          const randomIndex = Math.floor(Math.random() * images.length);
          this.randomImage = `assets/images/${images[randomIndex]}`;
          
          this.showSuccessAnimation = true;
          setTimeout(() => {
            this.showSuccessAnimation = false;
          }, 2000);
        }
      },
      error: () => {
      }
    });
  }
  
  loadPoints() {
    if (!this.authService.isLoggedIn()) return;
    
    this.pointService.getPoints().subscribe({
      next: (points) => {
        this.points = points;
        this.redrawCanvas();
      },
      error: () => {
      }
    });
  }
  
  clearPoints() {
    this.pointService.clearPoints().subscribe({
      next: () => {
        this.points = [];
        this.redrawCanvas();
        this.messageService.add({severity: 'success', summary: 'Успешно', detail: 'Все точки удалены'});
      },
      error: () => {
        this.messageService.add({severity: 'error', summary: 'Ошибка', detail: 'Ошибка удаления точек'});
      }
    });
  }
  
  redrawCanvas() {
    const canvas = document.getElementById('graph') as HTMLCanvasElement;
    if (!canvas) return;
    
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    
    const width = canvas.width;
    const height = canvas.height;
    const centerX = width / 2;
    const centerY = height / 2;
    const r = this.r || 3;
    const maxR = 5;
    const maxSize = 150;
    const scale = maxSize / maxR;
    
    ctx.fillStyle = 'white';
    ctx.fillRect(0, 0, width, height);
    
    ctx.fillStyle = '#e6b8d4';
    
    ctx.beginPath();
    ctx.arc(centerX, centerY, scale * r, Math.PI, Math.PI * 1.5);
    ctx.lineTo(centerX, centerY);
    ctx.closePath();
    ctx.fill();
    
    ctx.fillRect(centerX - scale * r, centerY, scale * r, scale * r);
    
    ctx.beginPath();
    ctx.moveTo(centerX, centerY);
    ctx.lineTo(centerX + scale * r, centerY);
    ctx.lineTo(centerX, centerY + scale * r);
    ctx.closePath();
    ctx.fill();
    
    ctx.strokeStyle = '#000';
    ctx.lineWidth = 2;
    ctx.beginPath();
    ctx.moveTo(0, centerY);
    ctx.lineTo(width, centerY);
    ctx.moveTo(centerX, 0);
    ctx.lineTo(centerX, height);
    ctx.stroke();
    
    ctx.beginPath();
    ctx.moveTo(width - 10, centerY - 5);
    ctx.lineTo(width, centerY);
    ctx.lineTo(width - 10, centerY + 5);
    ctx.moveTo(centerX - 5, 10);
    ctx.lineTo(centerX, 0);
    ctx.lineTo(centerX + 5, 10);
    ctx.stroke();
    
    ctx.fillStyle = '#000';
    ctx.font = '14px Arial';
    ctx.fillText('x', width - 15, centerY - 10);
    ctx.fillText('y', centerX + 10, 15);
    ctx.fillText('R', centerX + scale * r, centerY + 20);
    ctx.fillText('R/2', centerX + scale * r / 2, centerY + 20);
    ctx.fillText('-R', centerX - scale * r, centerY + 20);
    ctx.fillText('-R/2', centerX - scale * r / 2, centerY + 20);
    ctx.fillText('R', centerX + 10, centerY - scale * r);
    ctx.fillText('R/2', centerX + 10, centerY - scale * r / 2);
    ctx.fillText('-R', centerX + 10, centerY + scale * r);
    ctx.fillText('-R/2', centerX + 10, centerY + scale * r / 2);
    
    ctx.lineWidth = 1;
    ctx.beginPath();
    ctx.moveTo(centerX + scale * r, centerY - 5);
    ctx.lineTo(centerX + scale * r, centerY + 5);
    ctx.moveTo(centerX + scale * r / 2, centerY - 5);
    ctx.lineTo(centerX + scale * r / 2, centerY + 5);
    ctx.moveTo(centerX - scale * r, centerY - 5);
    ctx.lineTo(centerX - scale * r, centerY + 5);
    ctx.moveTo(centerX - scale * r / 2, centerY - 5);
    ctx.lineTo(centerX - scale * r / 2, centerY + 5);
    ctx.moveTo(centerX - 5, centerY - scale * r);
    ctx.lineTo(centerX + 5, centerY - scale * r);
    ctx.moveTo(centerX - 5, centerY - scale * r / 2);
    ctx.lineTo(centerX + 5, centerY - scale * r / 2);
    ctx.moveTo(centerX - 5, centerY + scale * r);
    ctx.lineTo(centerX + 5, centerY + scale * r);
    ctx.moveTo(centerX - 5, centerY + scale * r / 2);
    ctx.lineTo(centerX + 5, centerY + scale * r / 2);
    ctx.stroke();
    
    this.points.forEach(point => {
      this.drawPoint(point);
    });
  }
  
  drawPoint(point: Point) {
    const canvas = document.getElementById('graph') as HTMLCanvasElement;
    if (!canvas) return;
    
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    
    const centerX = canvas.width / 2;
    const centerY = canvas.height / 2;
    const maxR = 5;
    const maxSize = 150;
    const scale = maxSize / maxR;
    
    const x = centerX + point.x * scale;
    const y = centerY - point.y * scale;
    
    ctx.fillStyle = point.hit ? '#4caf50' : '#f44336';
    ctx.beginPath();
    ctx.arc(x, y, 5, 0, Math.PI * 2);
    ctx.fill();
  }
  
  onCanvasClick(event: MouseEvent) {
    if (!this.r) {
      return;
    }
    
    const canvas = event.target as HTMLCanvasElement;
    const rect = canvas.getBoundingClientRect();
    const clickX = event.clientX - rect.left;
    const clickY = event.clientY - rect.top;
    
    const centerX = canvas.width / 2;
    const centerY = canvas.height / 2;
    const maxR = 5;
    const maxSize = 150;
    const scale = maxSize / maxR;
    
    const rawX = (clickX - centerX) / scale;
    const rawY = (centerY - clickY) / scale;
    
    const x = Number(rawX.toFixed(2));
    const y = Number(rawY.toFixed(2));
    
    this.x = x;
    this.y = y;
    
    this.sendPointRequest(x, y, this.r);
  }
}
