import { createSlice } from '@reduxjs/toolkit'
import type { PayloadAction } from '@reduxjs/toolkit'
import type { IUserData } from '../../interfaces'

const initialState: IUserData = {
	token: null,
	tokenType: '',
	user: {
		id: null,
		organizationId: null,
		username: '',
		email: '',
		password: null,
		active: true,
	},
	organization: {
		id: null,
		name: '',
		orgType: '',
		inn: '',
		regionId: null,
	},
	roles: [],
	expired: null,
	valid: null,
	error: null,
	message: null,
}

const UserProfileSlice = createSlice({
	name: 'userProfile',
	initialState,
	reducers: {
		setData: (state, action: PayloadAction<IUserData>) => {
			state.token = action.payload.token
			state.tokenType = action.payload.tokenType
			state.user = action.payload.user
			state.organization = action.payload.organization
			state.roles = action.payload.roles
			state.expired = action.payload.expired
			state.valid = action.payload.valid
			state.error = action.payload.error
			state.message = action.payload.message
		},
	},
})

export const { setData } = UserProfileSlice.actions

export default UserProfileSlice.reducer
