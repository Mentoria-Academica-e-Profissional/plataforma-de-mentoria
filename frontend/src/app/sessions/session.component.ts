import { CommonModule, NgIf, Time } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { Mentor } from '../entity/mentor';
import { Mentored } from '../entity/mentored';
import { Session } from '../entity/session';
import { SessionService } from './session.service';
import { responseMentored } from '../entity/responses/response-mentored';
import { ProfileService } from '../profile/profile.service';
import { User } from '../entity/user';
import { MentorDTO } from '../entity/dto/mentor-dto';

@Component({
  selector: 'app-session',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './session.component.html',
  styleUrl: './session.component.css'
})
export class SessionComponent implements OnInit {
  title = 'Sessão';

  loading = false;
  mentors: MentorDTO[] | null = null;
  mentoreds: responseMentored[] | null = null;
  mentorId: number | null = null;
  mentoredId: number | null = null;
  timeSlots: string[] = [
    '00:00', '00:30', '01:00', '01:30', '02:00', '02:30', '03:00', '03:30',
    '04:00', '04:30', '05:00', '05:30', '06:00', '06:30', '07:00', '07:30',
    '08:00', '08:30', '09:00', '09:30', '10:00', '10:30', '11:00', '11:30',
    '12:00', '12:30', '13:00', '13:30', '14:00', '14:30', '15:00', '15:30',
    '16:00', '16:30', '17:00', '17:30', '18:00', '18:30', '19:00', '19:30',
    '20:00', '20:30', '21:00', '21:30', '22:00', '22:30', '23:00', '23:30',
  ];

  user: User| null = null;

  private authService = inject(AuthService);
  private sessionService = inject(SessionService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  sessionForm: FormGroup = this.fb.group({
    mentorId: [''],
    mentoredId: [''],
    date: ['', [Validators.required]],
    time: ['', [Validators.required]],
    meetingTopic: ['', [Validators.required]],
    status: [''],
    location: ['', [Validators.required]],
  })

  async ngOnInit(): Promise<void> {
    this.user = await this.authService.getCurrentUser();
    if(this.user?.role === 'MENTOR') {
      this.mentoreds = await this.authService.getAllMentored();
      this.mentorId = this.user?.id || null;
    }else if(this.user?.role === 'MENTORADO') {
      this.mentors = await this.authService.getAllMentor();
      this.mentoredId = this.user?.id || null;
    }
  }

  isMentor(): boolean {
    return this.user?.role === 'MENTOR';
  }

  onSubmit() {
    if (this.sessionForm.valid) {
      const sessionData: Session = {
        ...this.sessionForm.value,
        mentorId: this.sessionForm.value.mentorId || this.user?.id ,
        mentoredId: this.sessionForm.value.mentoredId || this.user?.id,
        status: 'PENDING',
      };

      this.sessionService.registerSession(sessionData).then(
        () => alert('Sessão registrada com sucesso!'),
      )
      .catch(() => 
        alert('Não foi possivel cadastrar a  essão!'),
      );
    }
  }
  goBack(): void {
    this.router.navigate(['/home']);
  }
}
