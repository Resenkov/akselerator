import React from 'react'
import { Card, CardContent, Typography, Stack } from '@mui/material'

const StatsSection: React.FC = () => {
	const stats = [
		{ value: '1,247', label: 'Уловов за месяц' },
		{ value: '42.5', label: 'Тонн рыбы' },
		{ value: '15', label: 'Активных рыбаков' },
		{ value: '87%', label: 'Квот использовано' },
	]

	return (
		<Stack direction='row' justifyContent='center' gap={2}>
			{stats.map((stat, index) => (
				<Card
					key={index}
					sx={{
						textAlign: 'center',
						py: 3,
						bgcolor: 'background.default',
						flex: 1,
					}}
				>
					<CardContent>
						<Typography
							variant='h4'
							component='div'
							color='primary'
							gutterBottom
							sx={{ fontWeight: 'bold' }}
						>
							{stat.value}
						</Typography>
						<Typography variant='body2' color='text.secondary'>
							{stat.label}
						</Typography>
					</CardContent>
				</Card>
			))}
		</Stack>
	)
}

export default StatsSection
