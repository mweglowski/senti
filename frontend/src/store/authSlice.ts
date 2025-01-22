import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface User {
  id: number;
  username: string;
  email: string;
  password: string;
  createdAt: string;
}

interface AuthState {
  isUserLoggedIn: boolean;
  user: User | null;
}

const initialState: AuthState = {
  isUserLoggedIn: false,
  user: null,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    login(state, action: PayloadAction<User>) {
      state.isUserLoggedIn = true;
      state.user = action.payload;
    },
    logout(state) {
      state.isUserLoggedIn = false;
      state.user = null;
    },
  },
});

export const { login, logout } = authSlice.actions;

export default authSlice.reducer;